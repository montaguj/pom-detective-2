package com.montague.utility.detective;

import com.montague.utility.pom.PomFile;
import lombok.Getter;
import org.apache.maven.pom._4_0.Dependency;

@Getter
public class Result {

    private final Dependency dependency;
    private final PomFile pomFile;

    public Result(Dependency dependency, PomFile pomFile) {
        this.dependency = dependency;
        this.pomFile = pomFile;
    }

    @Override
    public String toString() {
        Dependency dep = pomFile.getAsDependency();
        return String.format("dependency { %s , %s , %s  } in %s " , dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion(), dep.getArtifactId());
    }
}
