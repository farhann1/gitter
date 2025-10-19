package com.example.gitter.commands.commit;

import com.example.gitter.commands.strategy.CommandStrategy;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.ERROR_COMMIT_MESSAGE_REQUIRED;

public class CommitOptions {
    private final String message;
    private final boolean stageAll;
    
    private CommitOptions(Builder builder) {
        if (builder.messages == null || builder.messages.length == 0) {
            throw new IllegalArgumentException(ERROR_COMMIT_MESSAGE_REQUIRED);
        }
        
        this.message = String.join(NEWLINE + NEWLINE, builder.messages);
        this.stageAll = builder.stageAll;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String[] messages;
        private boolean stageAll = false;
        
        private Builder() {}
        
        public Builder messages(String[] messages) {
            this.messages = messages;
            return this;
        }
        
        public Builder stageAll(boolean stageAll) {
            this.stageAll = stageAll;
            return this;
        }
        
        public CommitOptions build() {
            return new CommitOptions(this);
        }
    }
    
    public CommandStrategy<CommitOptions> getStrategy() {
        if (stageAll) {
            return new StageAllCommitStrategy();
        }
        return new StandardCommitStrategy();
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isStageAll() {
        return stageAll;
    }
}
