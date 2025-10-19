package com.example.gitter.models;

import java.io.IOException;

import static com.example.gitter.constants.Constants.TAB;
import static com.example.gitter.constants.Messages.ERROR_REPOSITORY_CORRUPT;

/**
 * Represents a file entry in the index (staging area) and commit - <path>\t<hash>
 * 
 * NOTE: Future optimization opportunity:
 * Add stat caching (mtime, size) to avoid re-hashing unchanged files.
 */
public class FileEntry {
    private final String path;
    private final String hash;
    
    public FileEntry(String path, String hash) {
        this.path = path;
        this.hash = hash;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getHash() {
        return hash;
    }
    
    @Override
    public String toString() {
        return path + TAB + hash;
    }
    
    public static FileEntry fromString(String line) throws IOException {
        String[] parts = line.split(TAB);
        
        if (parts.length != 2) {
            throw new IOException(ERROR_REPOSITORY_CORRUPT);
        }
        
        return new FileEntry(parts[0], parts[1]);
    }
}
