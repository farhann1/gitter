package com.example.gitter.commands.status;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.OutputFormatter;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;

public class StatusStrategy implements CommandStrategy<StatusOptions> {
    
    @Override
    public Integer execute(StatusOptions options) throws IOException {
        String currentBranch = RepositoryState.getCurrentBranch();
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        OutputFormatter.displayStatus(currentBranch, status);
        return 0;
    }
}
