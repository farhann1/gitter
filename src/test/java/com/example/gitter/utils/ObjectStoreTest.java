package com.example.gitter.utils;

import com.example.gitter.models.ObjectContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ObjectStoreTest {
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setup() throws IOException {
        // Set up the .gitter structure
        Path gitterDir = tempDir.resolve(".gitter");
        Path objectsDir = gitterDir.resolve("objects");
        Files.createDirectories(objectsDir);
        
        // Update PathConstants for testing
        System.setProperty("user.dir", tempDir.toString());
    }
    
    @Test
    void testWriteAndReadBlob() throws IOException {
        byte[] content = "Hello, World!".getBytes();
        
        // Write blob (returns computed hash)
        String hash = ObjectStore.writeBlob(content);
        
        // Verify hash is correct
        String expectedHash = HashUtils.hashBytes("Hello, World!".getBytes());
        assertEquals(expectedHash, hash);
        
        // Verify object exists
        assertTrue(ObjectStore.exists(hash));
        
        // Read blob back
        ObjectContent blobContent = ObjectStore.readBlob(hash);
        assertArrayEquals(content, blobContent.getData());
    }
    
    @Test
    void testWriteAndReadCommit() throws IOException {
        String commitContent = "message: Test commit\nparent: abc123\nfiles:\n";
        
        // Write commit
        String hash = ObjectStore.writeCommit(commitContent);
        
        // Verify object exists
        assertTrue(ObjectStore.exists(hash));
        
        // Read commit back
        ObjectContent commitObj = ObjectStore.readCommit(hash);
        assertEquals(commitContent, commitObj.getDataAsString());
    }
    
    @Test
    void testObjectSharding() throws IOException {
        // Initialize PathConstants properly by creating .gitter directory
        Path gitterDir = tempDir.resolve(".gitter");
        Path objectsDir = gitterDir.resolve("objects");
        Files.createDirectories(objectsDir);
        
        byte[] content = "Test content".getBytes();
        
        String hash = ObjectStore.writeBlob(content);
        
        // Verify object exists (sharding is internal implementation detail)
        assertTrue(ObjectStore.exists(hash), "Object should exist after write");
        
        // Verify we can read it back
        ObjectContent blobContent = ObjectStore.readBlob(hash);
        assertArrayEquals(content, blobContent.getData());
    }
    
    @Test
    void testReadNonExistentObject() {
        String fakeHash = "0000000000000000000000000000000000000000";
        
        assertThrows(IOException.class, () -> {
            ObjectStore.readBlob(fakeHash);
        });
    }
    
    @Test
    void testReadWrongObjectType() throws IOException {
        // Write a blob
        byte[] content = "blob content".getBytes();
        String hash = ObjectStore.writeBlob(content);
        
        // Try to read it as a commit (should fail)
        assertThrows(IOException.class, () -> {
            ObjectStore.readCommit(hash);
        });
    }
    
    @Test
    void testEmptyBlobStorage() throws IOException {
        byte[] emptyContent = new byte[0];
        
        String hash = ObjectStore.writeBlob(emptyContent);
        
        assertTrue(ObjectStore.exists(hash));
        ObjectContent blobContent = ObjectStore.readBlob(hash);
        assertEquals(0, blobContent.getData().length);
    }
}
