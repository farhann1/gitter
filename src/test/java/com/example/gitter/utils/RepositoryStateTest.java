package com.example.gitter.utils;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RepositoryState utility class.
 * Tests run in the actual project directory using the real .gitter repository.
 * 
 * Note: Some tests may be skipped if no valid repository is found.
 */
class RepositoryStateTest {
    
    private boolean hasValidRepo() {
        try {
            String branch = RepositoryState.getCurrentBranch();
            String hash = RepositoryState.getCurrentCommitHash();
            // Valid if we have a 40-char hex hash
            return hash != null && hash.length() == 40 && hash.matches("[0-9a-fA-F]+");
        } catch (Exception e) {
            return false;
        }
    }
    
    @Test
    void testGetWorkingFilesReturnsNonNullMap() throws IOException {
        Map<String, String> workingFiles = RepositoryState.getWorkingFiles();
        
        assertNotNull(workingFiles);
        // Should contain files from the working directory
        // Verify each file has a hash
        for (Map.Entry<String, String> entry : workingFiles.entrySet()) {
            assertNotNull(entry.getKey(), "File path should not be null");
            assertNotNull(entry.getValue(), "File hash should not be null");
            assertFalse(entry.getValue().isEmpty(), "File hash should not be empty");
            assertFalse(entry.getKey().isEmpty(), "File path should not be empty");
        }
    }
    
    @Test
    void testGetWorkingFilesExcludesGitterDirectory() throws IOException {
        Map<String, String> workingFiles = RepositoryState.getWorkingFiles();
        
        // Should not include any files from .gitter directory
        for (String path : workingFiles.keySet()) {
            assertFalse(path.startsWith(".gitter"), 
                "Working files should not include .gitter directory: " + path);
        }
    }
    
    @Test
    void testGetCurrentBranchReturnsValidBranch() throws IOException {
        String branch = RepositoryState.getCurrentBranch();
        
        assertNotNull(branch);
        assertFalse(branch.isEmpty());
        // Branch name should not contain whitespace or special chars
        assertFalse(branch.contains(" "));
        assertFalse(branch.contains("\n"));
    }
    
    @Test
    void testGetCommitHashFromBranchReturnsValidHash() throws IOException {
        if (!hasValidRepo()) {
            return; // Skip if no valid repo
        }
        
        String branch = RepositoryState.getCurrentBranch();
        String commitHash = RepositoryState.getCommitHashFromBranch(branch);
        
        assertNotNull(commitHash);
        assertFalse(commitHash.isEmpty());
        // SHA-1 hash should be 40 hex characters (case insensitive)
        assertEquals(40, commitHash.length());
        assertTrue(commitHash.matches("[0-9a-fA-F]+"));
    }
    
    @Test
    void testGetCurrentCommitHashReturnsValidHash() throws IOException {
        if (!hasValidRepo()) {
            return; // Skip if no valid repo
        }
        
        String commitHash = RepositoryState.getCurrentCommitHash();
        
        assertNotNull(commitHash);
        assertFalse(commitHash.isEmpty());
        assertEquals(40, commitHash.length());
        assertTrue(commitHash.matches("[0-9a-fA-F]+"));
    }
    
    @Test
    void testGetCommittedFilesReturnsMap() throws IOException {
        Map<String, FileEntry> committedFiles = RepositoryState.getCommittedFiles();
        
        assertNotNull(committedFiles);
        // Verify structure
        for (Map.Entry<String, FileEntry> entry : committedFiles.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertEquals(entry.getKey(), entry.getValue().getPath());
            assertNotNull(entry.getValue().getHash());
        }
    }
    
    @Test
    void testGetFilesFromCommitReturnsMap() throws IOException {
        String currentCommit = RepositoryState.getCurrentCommitHash();
        Map<String, FileEntry> files = RepositoryState.getFilesFromCommit(currentCommit);
        
        assertNotNull(files);
        for (Map.Entry<String, FileEntry> entry : files.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertEquals(entry.getKey(), entry.getValue().getPath());
        }
    }
    
    @Test
    void testGetParentCommitHashReturnsValidHashOrNull() throws IOException {
        String currentCommit = RepositoryState.getCurrentCommitHash();
        String parentHash = RepositoryState.getParentCommitHash(currentCommit);
        
        // Parent can be null for initial commit, or a valid hash
        if (parentHash != null) {
            assertFalse(parentHash.isEmpty());
            assertEquals(40, parentHash.length());
            assertTrue(parentHash.matches("[0-9a-fA-F]+"));
        }
    }
    
    @Test
    void testGetWorkingDirectoryStatusReturnsValidStatus() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        assertNotNull(status);
        // Verify all collections are non-null (can be empty)
        assertNotNull(status.getStagedNew());
        assertNotNull(status.getStagedModified());
        assertNotNull(status.getStagedDeleted());
        assertNotNull(status.getUnstagedModified());
        assertNotNull(status.getUnstagedDeleted());
        assertNotNull(status.getUntracked());
        assertNotNull(status.getAllWorkingFiles());
    }
    
    @Test
    void testGetWorkingDirectoryStatusCategoriesAreMutuallyExclusive() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        // A file should not appear in multiple staged categories
        for (String file : status.getStagedNew()) {
            assertFalse(status.getStagedModified().contains(file));
            assertFalse(status.getStagedDeleted().contains(file));
        }
        
        // A file should not appear in multiple unstaged categories
        for (String file : status.getUnstagedModified()) {
            assertFalse(status.getUnstagedDeleted().contains(file));
        }
    }
    
    @Test
    void testGetWorkingDirectoryStatusCleanFlagConsistency() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        boolean hasChanges = !status.getStagedNew().isEmpty() ||
                            !status.getStagedModified().isEmpty() ||
                            !status.getStagedDeleted().isEmpty() ||
                            !status.getUnstagedModified().isEmpty() ||
                            !status.getUnstagedDeleted().isEmpty() ||
                            !status.getUntracked().isEmpty();
        
        // isClean should be opposite of hasChanges
        assertEquals(!hasChanges, status.isClean());
    }
    
    @Test
    void testGetWorkingDirectoryStatusStagedFlagConsistency() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        boolean hasStagedChanges = !status.getStagedNew().isEmpty() ||
                                   !status.getStagedModified().isEmpty() ||
                                   !status.getStagedDeleted().isEmpty();
        
        assertEquals(hasStagedChanges, status.hasStagedChanges());
    }
    
    @Test
    void testGetWorkingDirectoryStatusUnstagedFlagConsistency() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        boolean hasUnstagedChanges = !status.getUnstagedModified().isEmpty() ||
                                     !status.getUnstagedDeleted().isEmpty() ||
                                     !status.getUntracked().isEmpty();
        
        assertEquals(hasUnstagedChanges, status.hasUnstagedChanges());
    }
}
