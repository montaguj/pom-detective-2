package com.montague.utility;

import com.montague.utility.detective.DependencyFinder;
import org.apache.maven.pom._4_0.Dependency;

public class Main {

    public static void main(String[] args) {

        Dependency dependency = new Dependency();
        dependency.setArtifactId("proj-b");
        dependency.setGroupId("org.groupId");
        dependency.setVersion("0.0.1-SNAPSHOT");

        DependencyFinder df = DependencyFinder.builder().
                                    dependency(dependency).
                                    rootDir("src/test/resources/test-root").
                                    build();
        df.find();

    }
}
