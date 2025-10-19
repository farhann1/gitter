package com.example.gitter.models;

import java.nio.charset.StandardCharsets;

/**
 * Represents the parsed content of a Gitter object (blob, commit, tree)
 * Gitter objects have format: "type size\0content"
 */
public class ObjectContent {
    private final String type;
    private final int size;
    private final byte[] data;
    
    public ObjectContent(String type, int size, byte[] data) {
        this.type = type;
        this.size = size;
        this.data = data;
    }
    
    public String getType() {
        return type;
    }
    
    public int getSize() {
        return size;
    }
    
    public byte[] getData() {
        return data.clone();
    }
    
    public String getDataAsString() {
        return new String(data, StandardCharsets.UTF_8);
    }
}
