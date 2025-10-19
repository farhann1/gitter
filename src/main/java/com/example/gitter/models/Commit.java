package com.example.gitter.models;

import com.example.gitter.constants.Constants;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Serialization format:
 *      
 * message: <message>
 * <message line 2>
 * ...
 * timestamp: <timestamp>
 * parent: <parent>
 * files:
 * <file1 path>\t<file1 hash>
 * <file2 path>\t<file2 hash>
 * ...
 */
public class Commit {
    private static final String FIELD_MESSAGE = "message: ";
    private static final String FIELD_TIMESTAMP = "timestamp: ";
    private static final String FIELD_PARENT = "parent: ";
    private static final String FIELD_FILES = "files:";
    
    private final String hash;
    private final String message;
    private final String timestamp;
    private final String parent;
    private final Map<String, FileEntry> files;  // path -> FileEntry
    
    public Commit(String hash, String message, String timestamp, String parent, Map<String, FileEntry> files) {
        this.hash = hash;
        this.message = message;
        this.timestamp = timestamp;
        this.parent = parent;
        this.files = new HashMap<>(files);
    }
    
    public Commit(String message, String parent, Map<String, FileEntry> files) {
        this(null, message, Instant.now().toString(), parent, files);
    }
    
    public String getHash() {
        return hash;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public String getParent() {
        return parent;
    }
    
    public Map<String, FileEntry> getFiles() {
        return Collections.unmodifiableMap(files);
    }
    
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(FIELD_MESSAGE).append(message).append(Constants.NEWLINE);
        sb.append(FIELD_TIMESTAMP).append(timestamp).append(Constants.NEWLINE);
        sb.append(FIELD_PARENT).append(parent != null ? parent : "").append(Constants.NEWLINE);
        sb.append(FIELD_FILES).append(Constants.NEWLINE);
        for (FileEntry entry : files.values()) {
            sb.append(entry.toString()).append(Constants.NEWLINE);
        }
        return sb.toString();
    }
    
    public static Commit deserialize(String hash, String content) throws IOException {
        String[] lines = content.split(Constants.NEWLINE, -1);
        StringBuilder messageBuilder = new StringBuilder();
        String timestamp = null;
        String parent = null;
        Map<String, FileEntry> files = new HashMap<>();
        
        boolean readingMessage = false;
        boolean readingFiles = false;
        
        for (String line : lines) {
            if (line.startsWith(FIELD_MESSAGE)) {
                messageBuilder.append(line.substring(FIELD_MESSAGE.length()));
                readingMessage = true;
            } else if (line.startsWith(FIELD_TIMESTAMP)) {
                readingMessage = false;
                timestamp = line.substring(FIELD_TIMESTAMP.length());
            } else if (line.startsWith(FIELD_PARENT)) {
                String p = line.substring(FIELD_PARENT.length()).trim();
                parent = p.isEmpty() ? null : p;
            } else if (line.equals(FIELD_FILES)) {
                readingFiles = true;
            } else if (readingFiles && !line.trim().isEmpty()) {
                FileEntry entry = FileEntry.fromString(line);
                files.put(entry.getPath(), entry);
            } else if (readingMessage) {
                messageBuilder.append(Constants.NEWLINE).append(line);
            }
        }
        
        return new Commit(hash, messageBuilder.toString(), timestamp, parent, files);
    }
    
    public static Commit fromObjectContent(String hash, ObjectContent content) throws IOException {
        return deserialize(hash, content.getDataAsString());
    }
}
