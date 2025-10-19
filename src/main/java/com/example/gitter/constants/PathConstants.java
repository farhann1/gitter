package com.example.gitter.constants;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.gitter.constants.Constants.GITTER_DIR;
import static com.example.gitter.constants.Constants.USER_DIR;

public class PathConstants {
    private static final Path REPOSITORY_ROOT = findRepositoryRoot();
    public static final Path GITTER = REPOSITORY_ROOT.resolve(GITTER_DIR);
    public static final Path OBJECTS = GITTER.resolve("objects");
    public static final Path REFS = GITTER.resolve("refs");
    public static final Path HEADS = REFS.resolve("heads");
    public static final Path HEAD = GITTER.resolve("HEAD");
    public static final Path INDEX = GITTER.resolve("index");
    
    /**
     * Search upward from user.dir to find .gitter folder.
     */
    private static Path findRepositoryRoot() {
        Path current = Paths.get(System.getProperty(USER_DIR)).toAbsolutePath();
        
        while (current != null) {
            Path gitterDir = current.resolve(GITTER_DIR);
            if (Files.exists(gitterDir) && Files.isDirectory(gitterDir)) {
                return current;
            }
            current = current.getParent();
        }
        
        // Not found - return current directory
        return Paths.get(System.getProperty(USER_DIR));
    }
}
