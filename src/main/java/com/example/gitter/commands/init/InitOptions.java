package com.example.gitter.commands.init;

import com.example.gitter.commands.strategy.CommandStrategy;

public class InitOptions {
    
    private InitOptions(Builder builder) {}
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private Builder() {}
        
        public InitOptions build() {
            return new InitOptions(this);
        }
    }
    
    public CommandStrategy<InitOptions> getStrategy() {
        return new InitStrategy();
    }
}
