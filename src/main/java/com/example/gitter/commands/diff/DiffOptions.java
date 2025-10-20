package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;

public class DiffOptions {
    
    private DiffOptions(Builder builder) {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Builder() {}
        
        public DiffOptions build() {
            return new DiffOptions(this);
        }
    }
    
    public CommandStrategy<DiffOptions> getStrategy() {
        return new DiffStrategy();
    }
}
