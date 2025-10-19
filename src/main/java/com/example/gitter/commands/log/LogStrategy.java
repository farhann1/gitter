package com.example.gitter.commands.log;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.Commit;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.OutputFormatter;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.gitter.constants.Constants.MAX_LOG_COMMITS;
import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.LOG_NO_COMMITS;

public class LogStrategy implements CommandStrategy<LogOptions> {
    
    @Override
    public Integer execute(LogOptions options) throws IOException {
        List<Commit> commits = loadCommitHistory(MAX_LOG_COMMITS);
        
        if (commits.isEmpty()) {
            System.out.print(LOG_NO_COMMITS + NEWLINE);
            return 0;
        }
        
        for (Commit commit : commits) {
            OutputFormatter.displayCommit(commit);
        }
        
        return 0;
    }
    
    private List<Commit> loadCommitHistory(int maxCount) throws IOException {
        List<Commit> commits = new ArrayList<>();
        String currentHash = RepositoryState.getCurrentCommitHash();
        
        if (currentHash.isEmpty()) {
            return commits;
        }
        
        while (currentHash != null && !currentHash.isEmpty() && commits.size() < maxCount) {
            Commit commit = loadCommit(currentHash);
            if (commit == null) {
                break;
            }
            
            commits.add(commit);
            currentHash = commit.getParent();
        }
        
        return commits;
    }
    
    private Commit loadCommit(String hash) throws IOException {
        if (!ObjectStore.exists(hash)) {
            return null;
        }
        return Commit.fromObjectContent(hash, ObjectStore.readCommit(hash));
    }
}
