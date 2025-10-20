package com.example.gitter.utils;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IndexingTest {
    
    static Path testRoot;
    Path indexFile;
    
    @BeforeAll
    static void setupClass() throws IOException {
        testRoot = Paths.get("target/test-repo");
        
        // Set up the .gitter structure
        Path gitterDir = testRoot.resolve(".gitter");
        Files.createDirectories(gitterDir);
        Path indexPath = gitterDir.resolve("index");
        if (!Files.exists(indexPath)) {
            Files.createFile(indexPath);
        }
    }
    
    @BeforeEach
    void setup() throws IOException {
        indexFile = testRoot.resolve(".gitter/index");
        // Clear index before each test
        Files.writeString(indexFile, "");
    }
    
    @Test
    void testLoadEmptyIndex() throws IOException {
        Map<String, FileEntry> index = Indexing.loadIndex();
        
        assertNotNull(index);
        assertTrue(index.isEmpty());
    }
    
    @Test
    void testSaveAndLoadIndex() throws IOException {
        // Create some file entries
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456"),
            new FileEntry("subdir/file3.txt", "ghi789")
        );
        
        // Save to index
        Indexing.saveIndex(entries);
        
        // Load back
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        
        assertEquals(3, loadedIndex.size());
        assertTrue(loadedIndex.containsKey("file1.txt"));
        assertTrue(loadedIndex.containsKey("file2.txt"));
        assertTrue(loadedIndex.containsKey("subdir/file3.txt"));
        
        assertEquals("abc123", loadedIndex.get("file1.txt").getHash());
        assertEquals("def456", loadedIndex.get("file2.txt").getHash());
    }
    
    @Test
    void testSaveEmptyIndex() throws IOException {
        // Save empty collection
        Indexing.saveIndex(Collections.emptyList());
        
        // Verify index is empty
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertTrue(loadedIndex.isEmpty());
    }
    
    @Test
    void testClearIndex() throws IOException {
        // First add some entries
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456")
        );
        Indexing.saveIndex(entries);
        
        // Clear the index
        Indexing.clearIndex();
        
        // Verify it's empty
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertTrue(loadedIndex.isEmpty());
    }
    
    @Test
    void testIndexWithSpacesInFilename() throws IOException {
        List<FileEntry> entries = Collections.singletonList(
            new FileEntry("file with spaces.txt", "abc123")
        );
        
        Indexing.saveIndex(entries);
        
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertEquals(1, loadedIndex.size());
        assertTrue(loadedIndex.containsKey("file with spaces.txt"));
    }
    
    @Test
    void testIndexOverwrite() throws IOException {
        // Save first set of entries
        List<FileEntry> entries1 = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456")
        );
        Indexing.saveIndex(entries1);
        
        // Overwrite with second set
        List<FileEntry> entries2 = Collections.singletonList(
            new FileEntry("file3.txt", "ghi789")
        );
        Indexing.saveIndex(entries2);
        
        // Verify only second set exists
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertEquals(1, loadedIndex.size());
        assertTrue(loadedIndex.containsKey("file3.txt"));
        assertFalse(loadedIndex.containsKey("file1.txt"));
    }
    
    @Test
    void testStageFiles(@TempDir Path tempDir) throws IOException {
        // This test is complex because stageFiles() works with actual files
        // For now, we'll just test the empty case
        Map<String, FileEntry> indexMap = new HashMap<>();
        
        // Stage empty collection
        Indexing.stageFiles(Collections.emptySet(), indexMap);
        
        // Verify index is still empty
        assertTrue(indexMap.isEmpty());
    }
    
    @Test
    void testStageFilesEmpty() throws IOException {
        Map<String, FileEntry> indexMap = new HashMap<>();
        
        // Stage empty collection
        Indexing.stageFiles(Collections.emptySet(), indexMap);
        
        // Verify index is still empty
        assertTrue(indexMap.isEmpty());
    }
    
    @Test
    void testUnstageFiles() throws IOException {
        // First stage some files
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456"),
            new FileEntry("file3.txt", "ghi789")
        );
        Indexing.saveIndex(entries);
        
        // Unstage file2.txt
        Indexing.unstageFiles(Set.of("file2.txt"));
        
        // Verify file2.txt was removed
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertEquals(2, loadedIndex.size());
        assertTrue(loadedIndex.containsKey("file1.txt"));
        assertFalse(loadedIndex.containsKey("file2.txt"));
        assertTrue(loadedIndex.containsKey("file3.txt"));
    }
    
    @Test
    void testUnstageMultipleFiles() throws IOException {
        // Stage multiple files
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456"),
            new FileEntry("file3.txt", "ghi789")
        );
        Indexing.saveIndex(entries);
        
        // Unstage two files
        Indexing.unstageFiles(Set.of("file1.txt", "file3.txt"));
        
        // Verify only file2.txt remains
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertEquals(1, loadedIndex.size());
        assertTrue(loadedIndex.containsKey("file2.txt"));
        assertFalse(loadedIndex.containsKey("file1.txt"));
        assertFalse(loadedIndex.containsKey("file3.txt"));
    }
    
    @Test
    void testUnstageFilesEmpty() throws IOException {
        // Stage some files
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456")
        );
        Indexing.saveIndex(entries);
        
        // Unstage empty collection (should do nothing)
        Indexing.unstageFiles(Collections.emptySet());
        
        // Verify index unchanged
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertEquals(2, loadedIndex.size());
    }
    
    @Test
    void testStageModifiedFilesNoChanges() throws IOException {
        Map<String, FileEntry> indexMap = new HashMap<>();
        indexMap.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        
        // Create status with no unstaged changes
        Map<String, String> allWorkingFiles = new HashMap<>();
        allWorkingFiles.put("file1.txt", "abc123");
        
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(allWorkingFiles);
        
        // Note: stageModifiedFiles relies on status.getUnstagedModified() and getUnstagedDeleted()
        // which are populated by WorkingDirectoryStatus's categorization logic.
        // Since we're using a minimal constructor, these will be empty, so no changes occur.
        boolean changed = Indexing.stageModifiedFiles(indexMap, status);
        
        // No changes should be staged (because status has no unstaged changes)
        assertFalse(changed);
        assertEquals(1, indexMap.size());
    }
    
    @Test
    void testUpdateIndexWithEmptyCommitHash() throws IOException {
        // Stage files
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456")
        );
        Indexing.saveIndex(entries);
        
        // Update with empty hash should clear index
        Indexing.updateIndex("");
        
        // Verify index is cleared
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertTrue(loadedIndex.isEmpty());
    }
    
    @Test
    void testUpdateIndexWithNullCommitHash() throws IOException {
        // Stage files
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("file1.txt", "abc123"),
            new FileEntry("file2.txt", "def456")
        );
        Indexing.saveIndex(entries);
        
        // Update with null hash should clear index
        Indexing.updateIndex(null);
        
        // Verify index is cleared
        Map<String, FileEntry> loadedIndex = Indexing.loadIndex();
        assertTrue(loadedIndex.isEmpty());
    }
}
