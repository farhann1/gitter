package com.example.gitter.commands.reset;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.HEADS;

public class ResetToCommitStrategy implements CommandStrategy<ResetOptions> {
    
    @Override
    public Integer execute(ResetOptions options) throws IOException {
        String commitRef = options.isEmpty() ? HEAD_REF : options.getFirst();
        String targetCommitHash = resolveCommitHash(commitRef);
        
        if (targetCommitHash == null) {
            return 1;
        }
        
        String currentBranch = RepositoryState.getCurrentBranch();
        Path branchFile = HEADS.resolve(currentBranch);
        Files.writeString(branchFile, targetCommitHash + NEWLINE);
        
        Indexing.updateIndex(targetCommitHash);
        System.out.printf(RESET_HEAD_AT + NEWLINE, targetCommitHash.substring(0, HASH_SHORT_LENGTH));
        
        return 0;
    }
    
    private String resolveCommitHash(String commitRef) throws IOException {
        String targetCommitHash = RepositoryState.getCurrentCommitHash();
        if (targetCommitHash.isEmpty()) {
            String currentBranch = RepositoryState.getCurrentBranch();
            System.err.printf(ERROR_NO_COMMITS_YET + NEWLINE, currentBranch);
            return null;
        }
        
        if (commitRef.equals(HEAD_REF)) {
            return targetCommitHash;
        }
        
        int stepsBack = 0;
        if (commitRef.startsWith(HEAD_ANCESTOR_PREFIX)) {
            try {
                stepsBack = Integer.parseInt(commitRef.substring(HEAD_ANCESTOR_PREFIX_LENGTH));
            } catch (NumberFormatException e) {
                System.err.printf(ERROR_INVALID_COMMIT_REF + NEWLINE, commitRef);
                return null;
            }
        } else {
            System.err.printf(ERROR_INVALID_COMMIT_REF + NEWLINE, commitRef);
            return null;
        }
        
        for (int i = 0; i < stepsBack; i++) {
            String parent = RepositoryState.getParentCommitHash(targetCommitHash);
            
            if (parent == null || parent.isEmpty()) {
                System.err.printf(ERROR_CANNOT_GO_BACK_INITIAL + NEWLINE, stepsBack);
                return null;
            }
            
            targetCommitHash = parent;
        }
        
        return targetCommitHash;
    }
}
