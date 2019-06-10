package com.montague.utility.detective;

import com.montague.utility.pom.Pom;
import com.montague.utility.pom.PomFile;
import com.montague.utility.pom.PomFileReader;
import com.montague.utility.pom.PomReader;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.pom._4_0.Dependency;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.of;

@Slf4j
public class DependencyFinder {

    private final String rootDir;
    private final Dependency dependency;
    private List<Path> pathList;
    private final List<Result> results;
    private final PomReader reader;
    private final List<PomFile> pomFiles;

    @Builder
    private DependencyFinder(Dependency dependency, String rootDir) {
        this.rootDir = rootDir;
        this.dependency = dependency;
        this.results = new ArrayList<>();
        this.reader = new PomFileReader();
        this.pomFiles = new ArrayList<>();
    }

    public void find() {
        FileListProvider ff = new FileFinder();
        pathList = ff.find(rootDir, "pom.xml");
        readAllPomFiles();
        findResults(dependency);
        report();
    }

    private void findResults(Dependency lost) {
        log.info("interrogating pom files for " + lost.getArtifactId());
        for (PomFile pomFile : pomFiles) {
            Optional<Dependency> dependencyFound = pomFile.dependsOn(lost);
            if (dependencyFound.isPresent()) {
                log.info(format("found dependency in %s", DetectiveUtil.toString(pomFile.getAsDependency())));
                Dependency found = dependencyFound.get();
                if (found.getVersion() == null) {
                    handleNoVersion(lost, pomFile, found);
                } else if (found.getVersion().contains("$")) {
                    handleVersionAsProperty(lost, pomFile, found);
                } else {
                    handleVersionAsExplicitValue(lost, pomFile, found);
                }

                Dependency dependant = pomFile.getAsDependency();
                log.info(format("found that %s is needed by %s", lost.getArtifactId(), dependant.getArtifactId()));
                results.add(new Result(found, pomFile));
                findResults(dependant);
            }
        }
    }

    private void handleVersionAsExplicitValue(Dependency lost, PomFile pomFile, Dependency found) {
        log.info("dependency expressed explicitly");
        String version = found.getVersion();
        found.setVersion(version);
        warnOnDependencyDifference(lost, pomFile, found, version);
    }

    private void handleVersionAsProperty(Dependency lost, PomFile pomFile, Dependency found) {
        log.info("dependency has version expressed as property");
        Optional<String> version = findVersionFromProperty(found, pomFile);
        if (version.isPresent()) {
            found.setVersion(version.get());
            warnOnDependencyDifference(lost, pomFile, found, version.get());
        }
    }

    private void handleNoVersion(Dependency lost, PomFile pomFile, Dependency found) {
        log.info("dependency has no version, must be defined in dependency management");
        Optional<String> version = findVersionFromDependencyManagement(found, pomFile);
        if (version.isPresent()) {
            found.setVersion(version.get());
            warnOnDependencyDifference(lost, pomFile, found, version.get());
        }
    }

    private void warnOnDependencyDifference(Dependency lost, PomFile pom, Dependency found, String version) {
        if (!version.equals(lost.getVersion())) {
            log.warn(format("expected version %s in %s but found %s",
                    lost.getVersion(),
                    pom.getAsDependency().getArtifactId(),
                    found.getVersion()));
        }
    }

    private Optional<String> findVersionFromDependencyManagement(Dependency lost, PomFile pomFile) {
        Optional<Dependency> found = pomFile.managesDependencyOf(lost);
        if (found.isPresent()) {
            log.debug("found version in dependency management of file");
            if (found.get().getVersion().contains("$")) {
                log.debug("dependency has version expressed as property");
                Optional<String> version = findVersionFromProperty(found.get(), pomFile);
                if (version.isPresent()) {
                    return version;
                }
            }
            return of(found.get().getVersion());
        } else {
            Optional<PomFile> parentPom = findParentPom(pomFile);
            if (parentPom.isPresent()) {
                log.debug("found a parent pom");
                return findVersionFromDependencyManagement(lost, parentPom.get());
            } else {
                log.error("could not find parent pom " + DetectiveUtil.toString(lost));
                return Optional.empty();
            }
        }
    }

    private Optional<String> findVersionFromProperty(Dependency dependency, PomFile pomFile) {
        String versionProperty = dependency.getVersion().replace('$', ' ').replace('{', ' ').replace('}', ' ').trim();
        log.info(format("looking for: %s in %s", versionProperty, DetectiveUtil.toString(pomFile.getAsDependency())));
        Optional<String> p = pomFile.getProperty(versionProperty);
        if (p.isPresent()) {
            return p;
        } else {
            Optional<PomFile> parentPom = findParentPom(pomFile);
            if (parentPom.isPresent()) {
                return findVersionFromProperty(dependency, parentPom.get());
            } else {
                log.warn("could not find parent pom " + DetectiveUtil.toString(dependency));
            }
        }
        return Optional.empty();
    }


    private Optional<PomFile> findParentPom(PomFile pomFile) {
        Optional<Dependency> parent = pomFile.getParent();
        if (parent.isPresent()) {
            Dependency d = parent.get();
            for (PomFile p : pomFiles) {
                if (p.is(d.getGroupId(), d.getArtifactId(), d.getVersion())) {
                    return of(p);
                }
            }
        }
        return Optional.empty();

    }

    private void readAllPomFiles() {
        log.info("reading all the poms from " + rootDir);
        for (Path p : pathList) {
            String location = p.toAbsolutePath().toString();
            log.info(format("found %s", location));
            PomFile pom = new Pom(reader.getModelFromFile(location));
            pomFiles.add(pom);
        }
    }

    private void report() {
        log.info(format("reporting %s results", results.size()));
        for (Result r : results) {
            log.info(r.toString());
        }
    }
}
