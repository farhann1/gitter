package com.example.gitter.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.PathConstants.GITTER;

public class FileUtils {

    public static boolean isGitterInitialized() {
        return Files.exists(GITTER) && Files.isDirectory(GITTER);
    }

    public static boolean isInsideGitterDir(Path path) {
        return path.toAbsolutePath().startsWith(GITTER.toAbsolutePath());
    }

    public static Path getRelativePath(Path file) {
        Path workingDir = GITTER.getParent();
        return workingDir.relativize(file);
    }
   
    /**
     * Check if a file path matches the given pattern
     * Supports: "." (all files), glob patterns (*.txt), directories 
     */
    public static boolean matchesPattern(String relativePath, String pattern) {
        if (pattern.equals(CURRENT_DIR_PATTERN)) {
            return true;
        }
        
        if (pattern.contains(GLOB_WILDCARD_ASTERISK) || pattern.contains(GLOB_WILDCARD_QUESTION)) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher(GLOB_MATCHER_PREFIX + pattern);
            Path path = Path.of(relativePath);
            return matcher.matches(path) || matcher.matches(path.getFileName());
        }
        
        String normalizedPattern = pattern.endsWith(PATH_SEPARATOR) ? pattern : pattern + PATH_SEPARATOR;
        return relativePath.startsWith(normalizedPattern);
    }
    
    /**
     * Find all files matching user-provided patterns from a given file map.
     * 
     * @param patterns Array of user-provided patterns (relative to their current directory)
     * @param fileMap Map of available files to match against (path -> value)
     * @return Set of file paths that match any of the patterns
     */
    public static <T> Set<String> findMatchingFiles(String[] patterns, Map<String, T> fileMap) {
        if (patterns == null || patterns.length == 0) {
            return new HashSet<>();
        }
        
        Set<String> allMatches = new HashSet<>();
        for (String pattern : patterns) {
            Set<String> patternMatches = findMatchingFiles(pattern, fileMap);
            allMatches.addAll(patternMatches);
        }
        
        return allMatches;
    }
    
    /**
     * Find all files matching a single user-provided pattern from a given file map.
     * Supports: exact file paths, glob patterns (*.txt), and directories (subdir/)
     * 
     * @param pattern Single user-provided pattern (relative to their current directory)
     * @param fileMap Map of available files to match against (path -> value)
     * @return Set of file paths that match the pattern
     */
    public static <T> Set<String> findMatchingFiles(String pattern, Map<String, T> fileMap) {
        return findMatchingFiles(pattern, fileMap.keySet());
    }
    
    public static Set<String> findMatchingFiles(String pattern, Collection<String> filePaths) {
        String normalizedPattern = normalizePattern(pattern);
        Set<String> matches = new HashSet<>();
        
        if (filePaths.contains(normalizedPattern)) {
            matches.add(normalizedPattern);
        } else {
            for (String filePath : filePaths) {
                if (matchesPattern(filePath, normalizedPattern)) {
                    matches.add(filePath);
                }
            }
        }
        
        return matches;
    }

    /**
     * Normalize a user-provided pattern to be relative to repository root.
     * This allows commands like 'gitter add test.txt' to work from subdirectories.
     * 
     * @param pattern User-provided pattern (relative to their current directory)
     * @return Normalized pattern relative to repository root
     */
    public static String normalizePattern(String pattern) {
        if (pattern.contains(GLOB_WILDCARD_ASTERISK) || pattern.contains(GLOB_WILDCARD_QUESTION) || pattern.equals(CURRENT_DIR_PATTERN)) {
            return pattern;
        }       
        
        Path repoRoot = GITTER.getParent();
        Path currentDir = Path.of(System.getProperty(USER_DIR));
        Path absolutePath = currentDir.resolve(pattern).normalize();
        
        try {
            Path relativePath = repoRoot.relativize(absolutePath);
            return relativePath.toString();
        } catch (IllegalArgumentException e) {
            // Pattern is outside repository - return as is
            return pattern;
        }
    }
    
    /**
     * Delete a file and recursively remove any empty parent directories up to baseDir.
     *
     */
    public static void deleteFile(Path fileToDelete, Path baseDir) throws IOException {
        if (!Files.exists(fileToDelete)) {
            return;
        }
        
        Files.delete(fileToDelete);
        
        Path dir = fileToDelete.getParent();
        while (dir != null && !dir.equals(baseDir) && Files.exists(dir)) {
            try (var stream = Files.list(dir)) {
                if (stream.findAny().isEmpty()) {
                    Files.delete(dir);
                    dir = dir.getParent();
                } else {
                    break;
                }
            }
        }
    }
}
