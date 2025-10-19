package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;

import static com.example.gitter.constants.Messages.ERROR_FILE_PATH_REQUIRED;

public class DiffOptions {
    private final String file;
    
    private DiffOptions(Builder builder) {
        if (builder.file == null || builder.file.isEmpty()) {
            throw new IllegalArgumentException(ERROR_FILE_PATH_REQUIRED);
        }
        this.file = builder.file;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String file;
        
        private Builder() {}
        
        public Builder file(String file) {
            this.file = file;
            return this;
        }
        
        public DiffOptions build() {
            return new DiffOptions(this);
        }
    }
    
    public CommandStrategy<DiffOptions> getStrategy() {
        return new DiffStrategy();
    }
    
    public String getFile() {
        return file;
    }
}
