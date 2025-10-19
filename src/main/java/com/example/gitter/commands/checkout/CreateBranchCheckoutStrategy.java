package com.example.gitter.commands.checkout;

import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.HEAD;

public class CreateBranchCheckoutStrategy extends AbstractCheckoutStrategy {
    
    @Override
    protected boolean validateCheckout(CheckoutOptions options, Path branchFile) throws IOException {
        if (Files.exists(branchFile)) {
            System.err.print(String.format(ERROR_BRANCH_EXISTS, options.getBranch()) + NEWLINE);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void performCheckout(CheckoutOptions options, Path branchFile) throws IOException {
        String currentCommit = RepositoryState.getCurrentCommitHash();
        Files.writeString(branchFile, currentCommit + NEWLINE);
        Files.writeString(HEAD, options.getBranch() + NEWLINE);
    }
    
    @Override
    protected void displayResult(CheckoutOptions options) {
        System.out.print(String.format(CHECKOUT_SWITCHED_NEW, options.getBranch()) + NEWLINE);
    }
}
