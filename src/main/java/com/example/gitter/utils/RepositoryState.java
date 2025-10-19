package com.example.gitter.utils;

import com.example.gitter.models.Commit;
import com.example.gitter.models.FileEntry;
import com.example.gitter.models.ObjectContent;
import com.example.gitter.models.WorkingDirectoryStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.WARNING_COULD_NOT_PROCESS_FILE;
import static com.example.gitter.constants.PathConstants.*;

/**
 * Utility class for analyzing repository state (working directory, index, commits)
 * 
 * NOTE: Future optimization opportunity - Add stat caching to avoid re-hashing all files.
 * Current implementation hashes every file to check for changes, which is simpler but slower
 * for large repositories.
 */
public class RepositoryState {
    
    /**
     * Get all files in working directory with their hashes
     */
    public static Map<String, String> getWorkingFiles() throws IOException {
        Map<String, String> result = new HashMap<>();
        Path workingDir = GITTER.getParent();
        
        try (var stream = Files.walk(workingDir)) {
            stream.filter(Files::isRegularFile)
                  .filter(path -> !FileUtils.isInsideGitterDir(path))
                  .forEach(path -> {
                      try {
                          String relativePath = FileUtils.getRelativePath(path).toString();
                          String hash = HashUtils.hashFile(path);
                          result.put(relativePath, hash);
                      } catch (IOException e) {
                          System.err.println(String.format(WARNING_COULD_NOT_PROCESS_FILE, path, e.getMessage()));
                      }
                  });
        }
        
        return result;
    }

    public static String getCurrentBranch() throws IOException {
        if (!Files.exists(HEAD)) {
            return DEFAULT_BRANCH;
        }
        return Files.readString(HEAD).trim();
    }
    
    public static String getCommitHashFromBranch(String branchName) throws IOException {
        Path branchFile = HEADS.resolve(branchName);
        if (Files.exists(branchFile) && Files.size(branchFile) > 0) {
            return Files.readString(branchFile).trim();
        }

        return EMPTY_STRING;
    }
    
    /**
     * Get the current commit hash (composite: current branch → commit hash)
     */
    public static String getCurrentCommitHash() throws IOException {
        String currentBranch = getCurrentBranch();
        return getCommitHashFromBranch(currentBranch);
    }
    
    /**
     * Get all files from the last commit on current branch
     */
    public static Map<String, FileEntry> getCommittedFiles() throws IOException {
        return getFilesFromCommit(getCurrentCommitHash());
    }
    
    public static Map<String, FileEntry> getFilesFromCommit(String commitHash) throws IOException {
        if (commitHash == null || commitHash.isEmpty() || !ObjectStore.exists(commitHash)) {
            return new HashMap<>();
        }
        
        Commit commit = Commit.fromObjectContent(commitHash, ObjectStore.readCommit(commitHash));
        return commit.getFiles();
    }

    public static String getParentCommitHash(String commitHash) throws IOException {
        if (!ObjectStore.exists(commitHash)) {
            return null;
        }
        
        Commit commit = Commit.fromObjectContent(commitHash, ObjectStore.readCommit(commitHash));
        return commit.getParent();
    }
    
    /**
     * Get complete working directory status - categorizes all files
     * 
     * Categorizes files into:
     * - Staged new: Files in index but not in commit
     * - Staged modified: Files in index with different hash than commit
     * - Unstaged modified: Files in working dir with different hash than index
     * - Unstaged deleted: Files in index/commit but missing from working dir
     * - Untracked: Files in working dir but not in index or commit
     */
    public static WorkingDirectoryStatus getWorkingDirectoryStatus() throws IOException {
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        Map<String, FileEntry> committedFiles = getCommittedFiles();
        Map<String, String> workingFiles = getWorkingFiles();
        
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(workingFiles);
        
        categorizeStagedFiles(indexMap, committedFiles, status);
        categorizeWorkingFiles(indexMap, committedFiles, workingFiles, status);
        categorizeDeletedFiles(indexMap, committedFiles, workingFiles, status);
        
        return status;
    }
    
    /**
     * Categorize staged files by comparing index vs commit
     * 
     * Logic:
     * - If file is in index but not in commit - mark as staged new
     * - If file is in both but hash differs - mark as staged modified
     * - If file is in both and hash matches - no staging changes (implicitly unchanged)
     */
    private static void categorizeStagedFiles(
            Map<String, FileEntry> indexMap,
            Map<String, FileEntry> committedFiles,
            WorkingDirectoryStatus status) {
        
        for (FileEntry stagedEntry : indexMap.values()) {
            String path = stagedEntry.getPath();
            String stagedHash = stagedEntry.getHash();
            FileEntry committedEntry = committedFiles.get(path);
            
            if (committedEntry == null) {
                status.addStagedNew(path);
            } else if (!stagedHash.equals(committedEntry.getHash())) {
                status.addStagedModified(path);
            }
        }
    }
    
    /**
     * Categorize working directory files by comparing working vs staged/committed
     * 
     * Priority-based logic:
     * 1. If file is in index - compare working with index only (staged changes take priority)
     * 2. If file is not in index or commit - mark as untracked
     * 3. If file is in commit but not in index - compare working with commit
     */
    private static void categorizeWorkingFiles(
            Map<String, FileEntry> indexMap,
            Map<String, FileEntry> committedFiles,
            Map<String, String> workingFiles,
            WorkingDirectoryStatus status) {
        
        for (Map.Entry<String, String> entry : workingFiles.entrySet()) {
            String path = entry.getKey();
            String workingHash = entry.getValue();
            FileEntry stagedEntry = indexMap.get(path);
            FileEntry committedEntry = committedFiles.get(path);
            
            if (stagedEntry != null) {
                if (!stagedEntry.getHash().equals(workingHash)) {
                    status.addUnstagedModified(path);
                }
            } else if (committedEntry != null) {
                if (!committedEntry.getHash().equals(workingHash)) {
                    status.addUnstagedModified(path);
                }
            } else {
                status.addUntracked(path);
            }
        }
    }
    
    /**
     * Categorize deleted files (tracked files missing from working directory)
     * 
     * Logic:
     * - If file is in commit but NOT in index and NOT in working dir - staged deletion
     * - If file is in index but NOT in working dir - unstaged deletion
     * 
     * This method examines all tracked paths (union of index + commit) and checks
     * if they're missing from the working directory.
     */
    private static void categorizeDeletedFiles(
            Map<String, FileEntry> indexMap,
            Map<String, FileEntry> committedFiles,
            Map<String, String> workingFiles,
            WorkingDirectoryStatus status) {
        
        Set<String> allTrackedPaths = new HashSet<>();
        allTrackedPaths.addAll(indexMap.keySet());
        allTrackedPaths.addAll(committedFiles.keySet());
        
        for (String trackedPath : allTrackedPaths) {
            if (!workingFiles.containsKey(trackedPath)) {
                if (!indexMap.containsKey(trackedPath) && committedFiles.containsKey(trackedPath)) {
                    status.addStagedDeleted(trackedPath);
                } else if (indexMap.containsKey(trackedPath)) {
                    status.addUnstagedDeleted(trackedPath);
                }
            }
        }
    }
    
    /**
     * Restore working tree from sourceCommit to targetCommit.
     * 
     * @param sourceCommitHash Current commit hash (to determine deletions)
     * @param targetCommitHash Target commit hash (to restore files from)
     * @throws IOException if file operations fail
     */
    public static void restoreWorkingTree(String sourceCommitHash, String targetCommitHash) throws IOException {
        Path workingDir = GITTER.getParent();
        Map<String, FileEntry> sourceFiles = getFilesFromCommit(sourceCommitHash);
        Map<String, FileEntry> targetFiles = getFilesFromCommit(targetCommitHash);
        
        // Step 1: Delete files that exist in source but not in target
        for (String sourcePath : sourceFiles.keySet()) {
            if (!targetFiles.containsKey(sourcePath)) {
                Path fileToDelete = workingDir.resolve(sourcePath);
                FileUtils.deleteFile(fileToDelete, workingDir);
            }
        }
        
        // Step 2: Restore/update files from target commit
        for (FileEntry entry : targetFiles.values()) {
            Path workingFile = workingDir.resolve(entry.getPath());
            String blobHash = entry.getHash();
            
            if (ObjectStore.exists(blobHash)) {
                ObjectContent blobContent = ObjectStore.readBlob(blobHash);
                Files.createDirectories(workingFile.getParent());
                Files.write(workingFile, blobContent.getData());
            }
        }
    }
}
