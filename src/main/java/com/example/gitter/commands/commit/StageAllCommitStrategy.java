package com.example.gitter.commands.commit;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.Indexing;

import java.io.IOException;
import java.util.Map;

import static com.example.gitter.constants.Messages.ERROR_NOTHING_TO_COMMIT;

/**
 * Auto-stages modified/deleted tracked files before committing.
 * Does NOT stage untracked (new) files.
 */
public class StageAllCommitStrategy extends AbstractCommitStrategy {
    
    @Override
    protected boolean validateCommit(CommitOptions options, WorkingDirectoryStatus status) throws IOException {
        if (!status.hasStagedChanges() && !status.hasUnstagedChanges()) {
            System.err.println(ERROR_NOTHING_TO_COMMIT);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected PrepareResult prepareIndex(CommitOptions options, WorkingDirectoryStatus status) throws IOException {
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        
        boolean autoStagedChanges = Indexing.stageModifiedFiles(indexMap, status);
        boolean hadStagedChanges = status.hasStagedChanges();
        
        boolean hasAnyChanges = autoStagedChanges || hadStagedChanges;
        
        int changeCount = status.getStagedNew().size() + 
                         status.getStagedModified().size() + 
                         status.getStagedDeleted().size() +
                         status.getUnstagedModified().size() +
                         status.getUnstagedDeleted().size();
        
        return new PrepareResult(indexMap, hasAnyChanges, changeCount);
    }
}
