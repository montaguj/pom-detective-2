package com.montague.utility.detective;

import com.montague.utility.exception.FileReadException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileFinder implements FileListProvider {

    @Override
    public List<Path> find(String root, String contains) {
        File f = new File(root);
        if (f.isDirectory()) {


            List<Path> paths = new ArrayList<>();
            try {
                Files.walk(Paths.get(f.getAbsolutePath()))
                        .filter(a -> Files.isRegularFile(a) && a.getFileName().toString().contains(contains))
                        .forEach(p -> paths.add(p));
            } catch (Exception e) {
                throw new FileReadException("could not traverse directory " + root);
            }
            return paths;
        } else {
            throw new FileReadException("root provided is not a valid directory");
        }
    }
}
