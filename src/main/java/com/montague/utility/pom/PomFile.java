package com.montague.utility.pom;

import org.apache.maven.pom._4_0.Dependency;

import java.util.Optional;

public interface PomFile {

    boolean is(String groupId, String artifactId, String version);

    Optional<Dependency> dependsOn(Dependency dependency);

    Optional<Dependency> managesDependencyOf(Dependency dependency);

    Optional<String> getProperty(String name);

    Dependency getAsDependency();

    Optional<Dependency> getParent();
}
