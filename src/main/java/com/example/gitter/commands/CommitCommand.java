package com.example.gitter.commands;

import com.example.gitter.models.Commit;
import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.RepositoryState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.*;

@Command(name = "commit",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter commit -m <msg>"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Create a new commit containing the current contents of the index",
             "  with the given log message describing the changes.",
             "",
             "  The commit message should describe what changes are being made and why."
         },
         optionListHeading = "%nOPTIONS%n",
         sortOptions = false
)
public class CommitCommand implements Callable<Integer> {
    
    @Option(names = {"-m"}, 
            description = "Use the given <msg> as the commit message. Multiple -m options create separate paragraphs.",
            paramLabel = "<msg>",
            required = true)
    private String[] messages;
    
    @Option(names = {"-a"},
            description = "Automatically stage modified and deleted files before committing (does not add new files)")
    private boolean stageAll = false;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
            if (!status.hasStagedChanges() && !status.hasUnstagedChanges()) {
                System.err.println(ERROR_NOTHING_TO_COMMIT);
                return 1;
            }
            
            Map<String, FileEntry> indexMap = Indexing.loadIndex();
            boolean staged = status.hasStagedChanges();
            
            if(stageAll) {
                staged = staged || Indexing.stageModifiedFiles(indexMap, status);
            }
            
            if (!staged) {
                System.err.println(ERROR_NOTHING_TO_COMMIT);
                return 1;
            }
            
            String currentBranch = RepositoryState.getCurrentBranch();
            String parentCommit = RepositoryState.getCurrentCommitHash();
            
            // Combine multiple -m messages with blank lines
            String fullMessage = String.join(NEWLINE + NEWLINE, messages);
            
            // Create commit object
            Commit commit = new Commit(fullMessage, parentCommit, indexMap);
            String commitContent = commit.serialize();
            String commitHash = ObjectStore.writeCommit(commitContent);
            
            // Update branch pointer
            Path branchFile = HEADS.resolve(currentBranch);
            Files.writeString(branchFile, commitHash + NEWLINE);
            
            // Sync index to the new commit (index should match HEAD after commit)
            Indexing.updateIndex(commitHash);
            
            // Display first line of commit message
            String firstLine = messages[0];
            System.out.print(String.format(COMMIT_SUCCESS, currentBranch, commitHash.substring(0, HASH_SHORT_LENGTH), firstLine) + NEWLINE);
            System.out.print(String.format(COMMIT_FILES_CHANGED, indexMap.size()) + NEWLINE);
            
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_COMMIT + e.getMessage());
            return 1;
        }
    }
}
