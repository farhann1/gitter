package com.example.gitter.commands.commit;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.Commit;
import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static com.example.gitter.constants.Constants.HASH_SHORT_LENGTH;
import static com.example.gitter.constants.Messages.COMMIT_FILES_CHANGED;
import static com.example.gitter.constants.Messages.COMMIT_SUCCESS;
import static com.example.gitter.constants.Messages.ERROR_NOTHING_TO_COMMIT;
import static com.example.gitter.constants.PathConstants.HEADS;

/**
 * Template Method pattern: defines the commit algorithm skeleton.
 * Subclasses implement validateCommit() and prepareIndex().
 */
public abstract class AbstractCommitStrategy implements CommandStrategy<CommitOptions> {
    
    @Override
    public final Integer execute(CommitOptions options) throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        
        if (!validateCommit(options, status)) {
            return 1;
        }
        
        PrepareResult result = prepareIndex(options, status);
        
        if (!result.hasChanges()) {
            System.err.println(ERROR_NOTHING_TO_COMMIT);
            return 1;
        }
        
        String commitHash = createAndWriteCommit(options, result.getIndexMap());
        Indexing.updateIndex(commitHash);
        displayResult(options, result, commitHash);
        
        return 0;
    }
    
    protected abstract boolean validateCommit(CommitOptions options, WorkingDirectoryStatus status) throws IOException;
    protected abstract PrepareResult prepareIndex(CommitOptions options, WorkingDirectoryStatus status) throws IOException;
    
    protected static class PrepareResult {
        private final Map<String, FileEntry> indexMap;
        private final boolean hasChanges;
        private final int changeCount;
        
        public PrepareResult(Map<String, FileEntry> indexMap, boolean hasChanges, int changeCount) {
            this.indexMap = indexMap;
            this.hasChanges = hasChanges;
            this.changeCount = changeCount;
        }
        
        public Map<String, FileEntry> getIndexMap() {
            return indexMap;
        }
        
        public boolean hasChanges() {
            return hasChanges;
        }
        
        public int getChangeCount() {
            return changeCount;
        }
    }
    
    protected final String createAndWriteCommit(CommitOptions options, Map<String, FileEntry> indexMap) 
            throws IOException {
        String parentCommit = RepositoryState.getCurrentCommitHash();
        Commit commit = new Commit(options.getMessage(), parentCommit, indexMap);
        String commitHash = ObjectStore.writeCommit(commit.serialize());
        
        String currentBranch = RepositoryState.getCurrentBranch();
        Files.writeString(HEADS.resolve(currentBranch), commitHash);
        
        return commitHash;
    }
    
    protected final void displayResult(CommitOptions options, PrepareResult result, String commitHash) 
            throws IOException {
        String currentBranch = RepositoryState.getCurrentBranch();
        String shortHash = commitHash.substring(0, HASH_SHORT_LENGTH);
        
        System.out.println(String.format(COMMIT_SUCCESS, currentBranch, shortHash, options.getMessage()));
        System.out.println(String.format(COMMIT_FILES_CHANGED, result.getChangeCount()));
    }
}
