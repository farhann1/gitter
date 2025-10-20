package com.example.gitter.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static com.example.gitter.constants.Constants.GITTER_DIR;
import static com.example.gitter.constants.Constants.GITTERIGNORE_FILE;
import static com.example.gitter.constants.PathConstants.GITTER;

/**
 * Singleton that handles .gitterignore file parsing 
 * Supports directories and exact file paths (no patterns)
 */
public class GitterIgnore {
    private static GitterIgnore instance;
    private final Set<String> ignoredDirectories;
    private final Set<String> ignoredFiles;

    private GitterIgnore(Set<String> ignoredDirectories, Set<String> ignoredFiles) {
        this.ignoredDirectories = ignoredDirectories;
        this.ignoredFiles = ignoredFiles;
    }
    
    public static GitterIgnore getInstance() throws IOException {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }
    
    private static GitterIgnore load() throws IOException {
        Path ignoreFile = GITTER.getParent().resolve(GITTERIGNORE_FILE);
        Set<String> ignoredDirectories = new HashSet<>();
        Set<String> ignoredFiles = new HashSet<>();
        
        ignoredDirectories.add(GITTER_DIR);
        ignoredFiles.add(GITTERIGNORE_FILE);
        
        if (Files.exists(ignoreFile)) {
            Files.readAllLines(ignoreFile).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(line -> {
                    if (line.endsWith("/")) {
                        ignoredDirectories.add(line.substring(0, line.length() - 1));
                    } else {
                        ignoredFiles.add(line);
                    }
                });
        }
        
        return new GitterIgnore(ignoredDirectories, ignoredFiles);
    }
    
    public boolean shouldIgnore(String relativePath) {
        for (String dir : ignoredDirectories) {
            if (relativePath.equals(dir) || relativePath.startsWith(dir + "/")) {
                return true;
            }
        }
        
        if (ignoredFiles.contains(relativePath)) {
            return true;
        }
        
        return false;
    }
}
