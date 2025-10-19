package com.example.gitter.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilsTest {
    
    @Test
    void testHashBytes() {
        byte[] content = "Hello, World!".getBytes();
        String hash = HashUtils.hashBytes(content);
        
        assertNotNull(hash);
        assertEquals(40, hash.length()); // SHA-1 produces 40 character hex string
        
        // Same content should produce same hash
        String hash2 = HashUtils.hashBytes(content);
        assertEquals(hash, hash2);
    }
    
    @Test
    void testHashBytesDifferentContent() {
        byte[] content1 = "Hello, World!".getBytes();
        byte[] content2 = "Hello, World".getBytes();
        
        String hash1 = HashUtils.hashBytes(content1);
        String hash2 = HashUtils.hashBytes(content2);
        
        assertNotEquals(hash1, hash2);
    }
    
    @Test
    void testHashFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        Path testFile = tempDir.resolve("test.txt");
        String content = "Test file content";
        Files.writeString(testFile, content);
        
        // Hash the file
        String fileHash = HashUtils.hashFile(testFile);
        String bytesHash = HashUtils.hashBytes(content.getBytes());
        
        // File hash should match bytes hash of same content
        assertEquals(bytesHash, fileHash);
    }
    
    @Test
    void testHashEmptyBytes() {
        String hash = HashUtils.hashBytes(new byte[0]);
        
        assertNotNull(hash);
        assertEquals(40, hash.length());
        // SHA-1 of empty bytes is always the same
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", hash);
    }
    
    @Test
    void testHashConsistency() {
        byte[] content = "Consistent content".getBytes();
        
        // Hash multiple times
        String hash1 = HashUtils.hashBytes(content);
        String hash2 = HashUtils.hashBytes(content);
        String hash3 = HashUtils.hashBytes(content);
        
        // All should be identical
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }
}
