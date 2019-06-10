package com.montague.utility.pom;

import org.apache.maven.pom._4_0.Model;

public interface PomReader {

    Model getModelFromFile(String path);
}
