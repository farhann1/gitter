package com.example.gitter.commands.status;

import com.example.gitter.commands.strategy.CommandStrategy;

public class StatusOptions {
    
    private StatusOptions(Builder builder) {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private Builder() {}
        
        public StatusOptions build() {
            return new StatusOptions(this);
        }
    }
    
    public CommandStrategy<StatusOptions> getStrategy() {
        return new StatusStrategy();
    }
}
