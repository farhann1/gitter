package com.example.gitter.commands.checkout;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Path;

import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.HEADS;

public abstract class AbstractCheckoutStrategy implements CommandStrategy<CheckoutOptions> {
    
    @Override
    public final Integer execute(CheckoutOptions options) throws IOException {
        if (!canSwitch()) {
            return 1;
        }

        Path branchFile = HEADS.resolve(options.getBranch());
        
        if (!validateCheckout(options, branchFile)) {
            return 1;
        }
        
        performCheckout(options, branchFile);
        displayResult(options);
        
        return 0;
    }
    
    protected abstract boolean validateCheckout(CheckoutOptions options, Path branchFile) throws IOException;
    protected abstract void performCheckout(CheckoutOptions options, Path branchFile) throws IOException;
    protected abstract void displayResult(CheckoutOptions options);
    
    protected final boolean canSwitch() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        if (!status.isClean()) {
            System.err.println(ERROR_UNCOMMITTED_CHANGES);
            return false;
        }
        
        return true;
    }
}
