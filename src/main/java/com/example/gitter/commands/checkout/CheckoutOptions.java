package com.example.gitter.commands.checkout;

import com.example.gitter.commands.strategy.CommandStrategy;

import static com.example.gitter.constants.Messages.ERROR_BRANCH_NAME_REQUIRED;

public class CheckoutOptions {
    private final String branch;
    private final boolean createBranch;
    
    private CheckoutOptions(Builder builder) {
        if (builder.branch == null || builder.branch.isEmpty()) {
            throw new IllegalArgumentException(ERROR_BRANCH_NAME_REQUIRED);
        }
        
        this.branch = builder.branch;
        this.createBranch = builder.createBranch;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String branch;
        private boolean createBranch = false;
        
        private Builder() {}
        
        public Builder branch(String branch) {
            this.branch = branch;
            return this;
        }
        
        public Builder createBranch(boolean createBranch) {
            this.createBranch = createBranch;
            return this;
        }
        
        public CheckoutOptions build() {
            return new CheckoutOptions(this);
        }
    }
    
    public CommandStrategy<CheckoutOptions> getStrategy() {
        if (createBranch) {
            return new CreateBranchStrategy();
        }
        return new StandardCheckoutStrategy();
    }
    
    public String getBranch() {
        return branch;
    }
    
    public boolean isCreateBranch() {
        return createBranch;
    }
}
