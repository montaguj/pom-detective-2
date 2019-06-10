package com.montague.utility.pom;

import com.montague.utility.exception.PomReaderException;
import org.apache.maven.pom._4_0.Model;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PomFileReaderTest {


    @Test
    public void pomFileReaderTest() {
        PomReader r = new PomFileReader();
        Model m = r.getModelFromFile("src/test/resources/example-pom.xml");
        assertThat("cannot read pom file", m.getArtifactId(), is("jpademo"));
    }

    @Test(expected = PomReaderException.class)
    public void pomFileReaderExceptionTest() {
        PomReader r = new PomFileReader();
        r.getModelFromFile("src/test/resources/does-not-exist-pom.xml");
    }
}
