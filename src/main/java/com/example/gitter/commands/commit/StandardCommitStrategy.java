package com.example.gitter.commands.commit;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.Indexing;

import java.io.IOException;
import java.util.Map;

import static com.example.gitter.constants.Messages.ERROR_NOTHING_TO_COMMIT;

public class StandardCommitStrategy extends AbstractCommitStrategy {
    
    @Override
    protected boolean validateCommit(CommitOptions options, WorkingDirectoryStatus status) throws IOException {
        if (!status.hasStagedChanges()) {
            System.err.println(ERROR_NOTHING_TO_COMMIT);
            return false;
        }
        
        return true;
    }
    
    @Override
    protected PrepareResult prepareIndex(CommitOptions options, WorkingDirectoryStatus status) throws IOException {
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        return new PrepareResult(indexMap, true);
    }
}
