package com.example.gitter.commands;

import com.example.gitter.models.Commit;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.OutputFormatter;
import com.example.gitter.utils.RepositoryState;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.MAX_LOG_COMMITS;
import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.ERROR_NOT_INITIALIZED;
import static com.example.gitter.constants.Messages.ERROR_FAILED_TO_READ_LOG;
import static com.example.gitter.constants.Messages.LOG_NO_COMMITS;

@Command(name = "log",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter log"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Shows the commit logs.",
             "",
             "  List commits that are reachable by following the parent links from",
             "  the current branch HEAD, in reverse chronological order (newest first).",
             "",
             "  Displays up to 10 most recent commits. For each commit, the output shows:",
             "    - Commit hash (40-character SHA-1)",
             "    - Author information",
             "    - Commit date and time",
             "    - Commit message"
         }
)
public class LogCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            List<Commit> commits = loadCommitHistory(MAX_LOG_COMMITS);
            if (commits.isEmpty()) {
                System.out.print(LOG_NO_COMMITS + NEWLINE);
                return 0;
            }
            
            for (Commit commit : commits) {
                OutputFormatter.displayCommit(commit);
            }
            
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_READ_LOG + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Returns commits in chronological order (newest first) 
     * from the current branch up to maxCount
     */
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
