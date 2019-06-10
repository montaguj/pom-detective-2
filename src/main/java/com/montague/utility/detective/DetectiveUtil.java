package com.montague.utility.detective;

import org.apache.maven.pom._4_0.Dependency;

public class DetectiveUtil {

    public static String toString(Dependency dependency) {
        return String.format("dependency : { %s , %s , %s  } ", dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion());
    }
}
