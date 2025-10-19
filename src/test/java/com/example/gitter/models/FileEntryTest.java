package com.example.gitter.models;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileEntryTest {
    
    @Test
    void testFileEntryCreation() {
        FileEntry entry = new FileEntry("path/to/file.txt", "abc123");
        
        assertEquals("path/to/file.txt", entry.getPath());
        assertEquals("abc123", entry.getHash());
    }
    
    @Test
    void testToString() {
        FileEntry entry = new FileEntry("path/to/file.txt", "abc123");
        String result = entry.toString();
        
        assertEquals("path/to/file.txt\tabc123", result);
    }
    
    @Test
    void testFromString() throws IOException {
        String line = "path/to/file.txt\tabc123def456";
        FileEntry entry = FileEntry.fromString(line);
        
        assertEquals("path/to/file.txt", entry.getPath());
        assertEquals("abc123def456", entry.getHash());
    }
    
    @Test
    void testFromStringWithSpacesInPath() throws IOException {
        String line = "path/to/my file.txt\tabc123";
        FileEntry entry = FileEntry.fromString(line);
        
        assertEquals("path/to/my file.txt", entry.getPath());
        assertEquals("abc123", entry.getHash());
    }
    
    @Test
    void testFromStringInvalidFormat() {
        // Too few fields
        assertThrows(IOException.class, () -> {
            FileEntry.fromString("invalidformat");
        });
    }
    
    @Test
    void testSerializationRoundTrip() throws IOException {
        FileEntry original = new FileEntry("src/Main.java", "def789");
        String serialized = original.toString();
        FileEntry deserialized = FileEntry.fromString(serialized);
        
        assertEquals(original.getPath(), deserialized.getPath());
        assertEquals(original.getHash(), deserialized.getHash());
    }
}
