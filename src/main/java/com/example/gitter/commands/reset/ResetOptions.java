package com.example.gitter.commands.reset;

import com.example.gitter.commands.strategy.CommandStrategy;

import java.util.Collections;
import java.util.List;

import static com.example.gitter.constants.Constants.HEAD_REF;

public class ResetOptions {
    private final List<String> args;
    
    private ResetOptions(Builder builder) {
        this.args = builder.args != null ? Collections.unmodifiableList(builder.args) : List.of();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<String> args;
        
        private Builder() {}
        
        public Builder args(List<String> args) {
            this.args = args;
            return this;
        }
        
        public ResetOptions build() {
            return new ResetOptions(this);
        }
    }
    
    public CommandStrategy<ResetOptions> getStrategy() {
        if (!args.isEmpty() && args.get(0).startsWith(HEAD_REF)) {
            return new ResetToCommitStrategy();
        }
        
        if (args.isEmpty()) {
            return new ResetToCommitStrategy();
        }
        
        return new UnstageFilesStrategy();
    }
    
    public List<String> getArgs() {
        return args;
    }
    
    public boolean isEmpty() {
        return args.isEmpty();
    }
    
    public String getFirst() {
        return args.isEmpty() ? null : args.get(0);
    }
}
