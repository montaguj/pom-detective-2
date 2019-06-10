package com.montague.utility.detective;

import java.nio.file.Path;
import java.util.List;

public interface FileListProvider {

    List<Path> find(String root, String nameContains);
}
