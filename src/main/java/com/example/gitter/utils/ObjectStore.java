package com.example.gitter.utils;

import com.example.gitter.models.ObjectContent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.OBJECTS;

/**
 * All objects (blobs, commits, trees) are stored in a unified objects/ directory
 * using format: "type size\0content"
 * 
 * Files are sharded by hash for performance: objects/ab/cdef123
 */
public class ObjectStore {

    public static ObjectContent readBlob(String hash) throws IOException {
        return readObject(hash, OBJECT_TYPE_BLOB);
    }

    public static String writeBlob(byte[] content) throws IOException {
        return writeObject(OBJECT_TYPE_BLOB, content);
    }

    public static ObjectContent readCommit(String hash) throws IOException {
        return readObject(hash, OBJECT_TYPE_COMMIT);
    }
    
    public static String writeCommit(String commitContent) throws IOException {
        return writeObject(OBJECT_TYPE_COMMIT, commitContent.getBytes(StandardCharsets.UTF_8));
    }
    
    public static boolean exists(String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }
        
        Path objectPath = getObjectPath(hash);
        return Files.exists(objectPath);
    }

    private static Path getObjectPath(String hash) {
        // Shard: first 2 chars = directory, remaining = filename
        String dirName = hash.substring(HASH_SHARD_START, HASH_SHARD_END);
        String fileName = hash.substring(HASH_SHARD_END);
        return OBJECTS.resolve(dirName).resolve(fileName);
    }
    
    /**
     * Write any object type to the store
     * Uses format: "type size\0content" and sharding: objects/ab/cdef123...
     * 
     * @param type Object type ("blob", "commit", "tree")
     * @param content The object content
     */
    private static String writeObject(String type, byte[] content) throws IOException {
        String hash = HashUtils.hashBytes(content);
        String header = type + SPACE + content.length + NULL_BYTE;
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(headerBytes);
        outputStream.write(content);
        byte[] fullContent = outputStream.toByteArray();
        
        Path objectPath = getObjectPath(hash);
        Files.createDirectories(objectPath.getParent());
        Files.write(objectPath, fullContent);

        return hash;
    }
    
    /**
     * Read any object type from the store
     * Parses format: "type size\0content"
     * 
     * @param hash The SHA-1 hash
     * @param type The object type
     * @return The ObjectContent object
     * @throws IOException if object doesn't exist or is corrupted
     */
    private static ObjectContent readObject(String hash, String type) throws IOException {
        Path objectPath = getObjectPath(hash);
        if (!Files.exists(objectPath)) {
            throw new IOException(String.format(ERROR_OBJECT_NOT_FOUND, hash));
        }
        
        byte[] fullContent = Files.readAllBytes(objectPath);
        
        int nullByteIndex = -1;
        for (int i = 0; i < fullContent.length; i++) {
            if (fullContent[i] == NULL_BYTE_VALUE) {
                nullByteIndex = i;
                break;
            }
        }
        
        if (nullByteIndex == -1) {
            throw new IOException(ERROR_MALFORMED_OBJECT_NO_NULL);
        }
        
        // Parse header: "type size"
        String header = new String(fullContent, 0, nullByteIndex, StandardCharsets.UTF_8);
        String[] parts = header.split(SPACE);
        
        if (parts.length < 2) {
            throw new IOException(String.format(ERROR_MALFORMED_OBJECT_HEADER, header));
        }
        
        String objectType = parts[0];
        if (!type.equals(objectType)) {
            throw new IOException(String.format(ERROR_EXPECTED_OBJECT_TYPE, type, objectType));
        }

        int size;
        try {
            size = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IOException(String.format(ERROR_MALFORMED_OBJECT_HEADER, header));
        }
        
        // Extract content after null byte
        byte[] content = Arrays.copyOfRange(fullContent, nullByteIndex + 1, fullContent.length);        
        if (size != content.length) {
            throw new IOException(String.format(ERROR_OBJECT_SIZE_MISMATCH, size, content.length));
        }
        
        return new ObjectContent(objectType, size, content);
    }
}
