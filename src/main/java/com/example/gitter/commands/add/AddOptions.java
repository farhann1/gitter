package com.example.gitter.commands.add;

import com.example.gitter.commands.strategy.CommandStrategy;

import java.util.Collections;
import java.util.List;

import static com.example.gitter.constants.Messages.ERROR_FILE_PATTERN_REQUIRED;

public class AddOptions {
    private final List<String> files;
    
    private AddOptions(Builder builder) {
        if (builder.files == null || builder.files.isEmpty()) {
            throw new IllegalArgumentException(ERROR_FILE_PATTERN_REQUIRED);
        }
        this.files = Collections.unmodifiableList(builder.files);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<String> files;
        
        private Builder() {}
        
        public Builder files(List<String> files) {
            this.files = files;
            return this;
        }
        
        public AddOptions build() {
            return new AddOptions(this);
        }
    }
    
    public CommandStrategy<AddOptions> getStrategy() {
        return new AddStrategy();
    }
    
    public List<String> getFiles() {
        return files;
    }
}
