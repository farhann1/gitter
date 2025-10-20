package com.example.gitter.utils;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.ObjectContent;
import com.example.gitter.models.WorkingDirectoryStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.GITTER;
import static com.example.gitter.constants.PathConstants.INDEX;

/**
 * Utility class for index (staging area) operations
 */
public class Indexing {

    /**
     * Load index as a map (path -> FileEntry) for quick lookup
     */
    public static Map<String, FileEntry> loadIndex() throws IOException {
        Map<String, FileEntry> indexMap = new HashMap<>();
        if (Files.exists(INDEX) && Files.size(INDEX) > 0) {
            List<String> lines = Files.readAllLines(INDEX);
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    FileEntry entry = FileEntry.fromString(line);
                    indexMap.put(entry.getPath(), entry);
                }
            }
        }
        
        return indexMap;
    }

    /**
     * Save index (staged files) to disk
     * 
     * @param stagedFiles Collection of files to save to index
     */
    public static void saveIndex(Collection<FileEntry> stagedFiles) throws IOException {
        String content = stagedFiles.stream()
                .map(entry -> entry.toString() + NEWLINE)
                .collect(Collectors.joining());
        Files.writeString(INDEX, content);
    }

    /**
     * Update the index to match a specific commit
     *
     * @param commitHash The commit hash to sync the index to
     * @throws IOException if the commit object doesn't exist (repository corruption)
     */
    public static void updateIndex(String commitHash) throws IOException {
        if (commitHash == null || commitHash.isEmpty()) {
            clearIndex();
            return;
        }

        if (!ObjectStore.exists(commitHash)) {
            throw new IOException(String.format(ERROR_OBJECT_NOT_FOUND, commitHash));
        }

        ObjectContent commitObj = ObjectStore.readCommit(commitHash);
        String[] lines = commitObj.getDataAsString().split(NEWLINE);
        List<FileEntry> entries = new ArrayList<>();
        for(int i=lines.length-1; i>=0; i--) {
            if (lines[i].startsWith(FILES_SECTION_MARKER)) {
                break;
            } else if (!lines[i].trim().isEmpty()) {
                entries.add(FileEntry.fromString(lines[i]));
            }
        }

        saveIndex(entries);
    }

    /**
     * Clear the index (remove all staged files)
     */
    public static void clearIndex() throws IOException {
        Files.writeString(INDEX, EMPTY_STRING);
    }
    
    /**
     * Stage all modified and deleted tracked files (but not new untracked files).
     * 
     * @param indexMap The current index map to update
     * @param status The working directory status (pre-computed)
     * @return true if any files were staged, false otherwise
     * @throws IOException if file operations fail
     */
    public static boolean stageModifiedFiles(Map<String, FileEntry> indexMap, WorkingDirectoryStatus status) throws IOException {
        Map<String, String> workingFiles = status.getAllWorkingFiles();
        Path workingDir = GITTER.getParent();
        boolean anyChanges = false;
        
        // Stage all unstaged modified files
        for (String path : status.getUnstagedModified()) {
            String currentHash = workingFiles.get(path);
            
            // Check if object already exists (avoid re-writing)
            if (!ObjectStore.exists(currentHash)) {
                Path sourceFile = workingDir.resolve(path);
                byte[] content = Files.readAllBytes(sourceFile);
                currentHash = ObjectStore.writeBlob(content);
            }
            
            indexMap.put(path, new FileEntry(path, currentHash));
            anyChanges = true;
        }
        
        // Stage all deletions (remove from index)
        for (String path : status.getUnstagedDeleted()) {
            indexMap.remove(path);
            anyChanges = true;
        }

        saveIndex(indexMap.values());
        return anyChanges;
    }
    
    /**
     * Stage the specified files by reading their content and storing in object store.
     * 
     * @param filePaths Collection of file paths to stage
     * @param indexMap The current index map to update
     * @throws IOException if file operations fail
     */
    public static void stageFiles(Collection<String> filePaths, Map<String, FileEntry> indexMap) throws IOException {
        Path workingDir = GITTER.getParent();
        for (String relativePath : filePaths) {
            Path sourceFile = workingDir.resolve(relativePath);
            byte[] content = Files.readAllBytes(sourceFile);
            String actualHash = ObjectStore.writeBlob(content);
            indexMap.put(relativePath, new FileEntry(relativePath, actualHash));
        }
    }
    
    /**
     * Unstage the specified files from the index.
     * 
     * @param filePaths Set of file paths to remove from index
     * @throws IOException if file operations fail
     */
    public static void unstageFiles(Set<String> filePaths) throws IOException {
        if (filePaths.isEmpty()) {
            return;
        }
        
        Map<String, FileEntry> indexMap = loadIndex();
        for (String file : filePaths) {
            indexMap.remove(file);
        }
        
        saveIndex(indexMap.values());
    }
}
