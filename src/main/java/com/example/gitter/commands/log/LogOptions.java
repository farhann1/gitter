package com.example.gitter.commands.log;

import com.example.gitter.commands.strategy.CommandStrategy;

public class LogOptions {
    
    private LogOptions(Builder builder) {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private Builder() {}
        
        public LogOptions build() {
            return new LogOptions(this);
        }
    }
    
    public CommandStrategy<LogOptions> getStrategy() {
        return new LogStrategy();
    }
}
