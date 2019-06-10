package com.montague.utility.detective;

import com.montague.utility.exception.FileReadException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

public class FileFinderTest {

    @Test(expected = FileReadException.class)
    public void exceptionThrownWhenNotRootDirectory() {
        new FileFinder().find("src/test/resources/not-a-root-dir", "pom.xml");
    }

    @Test
    public void successfullyFindRootDir() {
        List<Path> paths =
                new FileFinder().find("src/test/resources/test-root", "pom.xml");
        Assert.assertThat(paths.size(), Matchers.is(4));
    }
}
