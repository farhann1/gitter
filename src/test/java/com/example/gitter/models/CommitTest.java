package com.example.gitter.models;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommitTest {
    
    @Test
    void testCommitCreation() {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        files.put("file2.txt", new FileEntry("file2.txt", "def456"));
        
        Commit commit = new Commit("Test commit", null, files);
        
        assertEquals("Test commit", commit.getMessage());
        assertNull(commit.getParent());
        assertEquals(2, commit.getFiles().size());
        assertNotNull(commit.getTimestamp());
        assertNull(commit.getHash()); // Hash is set during deserialization
    }
    
    @Test
    void testCommitWithParent() {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        
        String parentHash = "parent123abc";
        Commit commit = new Commit("Child commit", parentHash, files);
        
        assertEquals("Child commit", commit.getMessage());
        assertEquals(parentHash, commit.getParent());
    }
    
    @Test
    void testCommitSerialization() {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        files.put("file2.txt", new FileEntry("file2.txt", "def456"));
        
        Commit commit = new Commit("Test message", "parent123", files);
        String serialized = commit.serialize();
        
        // Verify structure
        assertTrue(serialized.contains("message: Test message"));
        assertTrue(serialized.contains("parent: parent123"));
        assertTrue(serialized.contains("files:"));
        assertTrue(serialized.contains("file1.txt\tabc123"));
        assertTrue(serialized.contains("file2.txt\tdef456"));
        assertTrue(serialized.contains("timestamp:"));
    }
    
    @Test
    void testCommitSerializationWithoutParent() {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        
        Commit commit = new Commit("Initial commit", null, files);
        String serialized = commit.serialize();
        
        // With null parent, should contain "parent: " (empty value) based on implementation
        assertTrue(serialized.contains("message: Initial commit"));
        assertTrue(serialized.contains("files:"));
    }
    
    @Test
    void testCommitDeserialization() throws IOException {
        String commitContent = "message: Test commit\n" +
                              "timestamp: " + Instant.now().toString() + "\n" +
                              "parent: abc123parent\n" +
                              "files:\n" +
                              "file1.txt\thash1\n" +
                              "file2.txt\thash2\n";
        
        String commitHash = "commit123abc";
        Commit commit = Commit.deserialize(commitHash, commitContent);
        
        assertEquals("Test commit", commit.getMessage());
        assertEquals("abc123parent", commit.getParent());
        assertEquals(commitHash, commit.getHash());
        assertEquals(2, commit.getFiles().size());
    }
    
    @Test
    void testCommitDeserializationWithoutParent() throws IOException {
        String commitContent = "message: Initial commit\n" +
                              "timestamp: " + Instant.now().toString() + "\n" +
                              "files:\n" +
                              "file1.txt\thash1\n";
        
        Commit commit = Commit.deserialize("hash123", commitContent);
        
        assertEquals("Initial commit", commit.getMessage());
        assertNull(commit.getParent());
    }
    
    @Test
    void testCommitWithMultilineMessage() {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("file1.txt", new FileEntry("file1.txt", "abc123"));
        
        String multilineMessage = "Title\n\nDescription paragraph 1\n\nDescription paragraph 2";
        Commit commit = new Commit(multilineMessage, null, files);
        
        assertEquals(multilineMessage, commit.getMessage());
        
        String serialized = commit.serialize();
        assertTrue(serialized.contains("message: " + multilineMessage));
    }
    
    @Test
    void testSerializationRoundTrip() throws IOException {
        Map<String, FileEntry> files = new HashMap<>();
        files.put("src/Main.java", new FileEntry("src/Main.java", "abc123"));
        files.put("test/Test.java", new FileEntry("test/Test.java", "def456"));
        
        Commit original = new Commit("Round trip test", "parent999", files);
        String serialized = original.serialize();
        
        // Simulate storing and retrieving
        String hash = "generatedHash123";
        Commit deserialized = Commit.deserialize(hash, serialized);
        
        assertEquals(original.getMessage(), deserialized.getMessage());
        assertEquals(original.getParent(), deserialized.getParent());
        assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        assertEquals(original.getFiles().size(), deserialized.getFiles().size());
        assertEquals(hash, deserialized.getHash());
    }
}
