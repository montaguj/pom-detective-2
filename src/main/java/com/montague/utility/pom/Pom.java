package com.montague.utility.pom;

import org.apache.maven.pom._4_0.Dependency;
import org.apache.maven.pom._4_0.DependencyManagement;
import org.apache.maven.pom._4_0.Model;
import org.apache.maven.pom._4_0.Parent;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Optional;

public class Pom implements PomFile {

    private final Model model;

    public Pom(Model m) {
        this.model = m;
    }

    @Override
    public boolean is(String groupId, String artifactId, String version) {
        return groupId.equals(model.getGroupId()) &&
                artifactId.equals(model.getArtifactId()) &&
                version.equals(model.getVersion());
    }

    @Override
    public Optional<Dependency> dependsOn(Dependency dependency) {
        Model.Dependencies dependencies = model.getDependencies();
        if (dependencies != null) {
            List<Dependency> dep = dependencies.getDependency();
            if (!dep.isEmpty()) {
                return dep.stream().filter(d -> matchesArtifactAndGroup(d, dependency) ||
                        hasFullyQualifiedDependency(d, dependency)).findFirst();
            }

        }
        return Optional.empty();
    }

    @Override
    public Optional<Dependency> managesDependencyOf(Dependency dependency) {

        DependencyManagement management = model.getDependencyManagement();
        if (model.getDependencyManagement() != null &&
                model.getDependencyManagement().getDependencies() != null) {

            DependencyManagement.Dependencies dep = management.getDependencies();
            if (!dep.getDependency().isEmpty()) {
                return dep.getDependency().stream().filter(d -> matchesArtifactAndGroup(d, dependency) ||
                        hasFullyQualifiedDependency(d, dependency)).findFirst();

            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getProperty(String name) {
        Model.Properties p = model.getProperties();
        if (p != null) {
            List<Element> props = p.getAny();
            for (Element e : props) {
                if (e.getTagName().equals(name)) {
                    return Optional.of(e.getFirstChild().getNodeValue());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Dependency getAsDependency() {
        Dependency d = new Dependency();
        d.setGroupId(model.getGroupId());
        d.setArtifactId(model.getArtifactId());
        d.setVersion(model.getVersion());
        return d;
    }

    @Override
    public Optional<Dependency> getParent() {
        Optional<Parent> parent = Optional.ofNullable(model.getParent());
        if (!parent.isPresent()) {
            return Optional.empty();
        } else {
            Dependency d = new Dependency();
            d.setGroupId(parent.get().getGroupId());
            d.setArtifactId(parent.get().getArtifactId());
            d.setVersion(parent.get().getVersion());
            return Optional.of(d);
        }

    }

    private boolean matchesArtifactAndGroup(Dependency a, Dependency b) {
        return a.getArtifactId().equals(b.getArtifactId()) &&
                a.getGroupId().equals(b.getGroupId());
    }

    private boolean hasFullyQualifiedDependency(Dependency a, Dependency b) {
        return a.getArtifactId().equals(b.getArtifactId()) &&
                a.getGroupId().equals(b.getGroupId()) &&
                a.getVersion().equals(b.getVersion());
    }

}
