package com.example.gitter.constants;

public class Constants {
    public static final String USER_DIR = "user.dir";
    public static final String GITTER_DIR = ".gitter";
    public static final String DEFAULT_BRANCH = "main";
    public static final String NEWLINE = "\n";
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    
    // File pattern matching
    public static final String CURRENT_DIR_PATTERN = ".";
    public static final String GLOB_WILDCARD_ASTERISK = "*";
    public static final String GLOB_WILDCARD_QUESTION = "?";
    public static final String PATH_SEPARATOR = "/";
    public static final String GLOB_MATCHER_PREFIX = "glob:";
    
    // Commit format
    public static final String FILES_SECTION_MARKER = "files:";
    
    // Commit references
    public static final String HEAD_REF = "HEAD";
    public static final String HEAD_ANCESTOR_PREFIX = "HEAD~";
    public static final int HEAD_ANCESTOR_PREFIX_LENGTH = 5;
    
    // Hashing
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_SHORT_LENGTH = 7;
    
    // Empty/Utility
    public static final String EMPTY_STRING = "";
    
    // Object store
    public static final String OBJECT_TYPE_BLOB = "blob";
    public static final String OBJECT_TYPE_COMMIT = "commit";
    public static final String NULL_BYTE = "\0";
    public static final byte NULL_BYTE_VALUE = 0;
    public static final int HASH_SHARD_START = 0;
    public static final int HASH_SHARD_END = 2;
    
    public static final int MAX_LOG_COMMITS = 10;
    public static final int DIFF_CONTEXT_LINES = 3;
}
