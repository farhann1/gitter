package com.example.gitter.commands.checkout;

import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.HEAD;

public class StandardCheckoutStrategy extends AbstractCheckoutStrategy {
    
    @Override
    protected boolean validateCheckout(CheckoutOptions options, Path branchFile) throws IOException {
        if (!Files.exists(branchFile)) {
            System.err.print(String.format(ERROR_BRANCH_NOT_FOUND, options.getBranch()) + NEWLINE);
            return false;
        }
        
        String currentBranch = RepositoryState.getCurrentBranch();
        if (currentBranch.equals(options.getBranch())) {
            System.out.print(String.format(CHECKOUT_ALREADY_ON, options.getBranch()) + NEWLINE);
            return false;
        }
        
        return canSwitch();
    }
    
    @Override
    protected void performCheckout(CheckoutOptions options, Path branchFile) throws IOException {
        String sourceCommit = RepositoryState.getCurrentCommitHash();
        String targetCommit = RepositoryState.getCommitHashFromBranch(options.getBranch());
        
        Files.writeString(HEAD, options.getBranch() + NEWLINE);
        Indexing.updateIndex(targetCommit);
        RepositoryState.restoreWorkingTree(sourceCommit, targetCommit);
    }
    
    @Override
    protected void displayResult(CheckoutOptions options) {
        System.out.print(String.format(CHECKOUT_SWITCHED, options.getBranch()) + NEWLINE);
    }
}
