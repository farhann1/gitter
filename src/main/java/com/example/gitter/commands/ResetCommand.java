package com.example.gitter.commands;

import com.example.gitter.models.FileEntry;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.*;

@Command(name = "reset",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter reset [<commit>]",
             "  gitter reset [<pathspec>...]"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Reset current HEAD to the specified state.",
             "",
             "  In the first form (with commit), resets the current branch head to <commit>",
             "  and resets the index (but not the working tree) to match. This leaves all",
             "  your changed files as \"Changes not staged for commit\".",
             "",
             "  In the second form (with pathspec), unstages files from the staging area.",
             "",
             "  Common usage:",
             "    gitter reset HEAD~1      # Undo last commit, keep changes unstaged",
             "    gitter reset HEAD~2      # Undo last 2 commits",
             "    gitter reset file.txt    # Unstage specific file"
         },
         parameterListHeading = "%nARGUMENTS%n"
)
public class ResetCommand implements Callable<Integer> {
    
    @Parameters(paramLabel = "<commit-or-file>",
                description = "Commit reference (e.g., HEAD~1) or file to unstage",
                arity = "0..*")
    private List<String> args = new ArrayList<>();
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            if (!args.isEmpty() && args.get(0).startsWith(HEAD_REF)) {
                return resetToCommit(args.get(0));
            }
            
            if (args.isEmpty()) {
                // Reset all - reset index to HEAD
                return resetToCommit(HEAD_REF);
            } else {
                // Reset specific files
                Set<String> filesToUnstage = findFilesToUnstage();
                if (filesToUnstage.isEmpty()) {
                    System.out.println(RESET_NO_FILES_UNSTAGED);
                } else {
                    Indexing.unstageFiles(filesToUnstage);
                    System.out.println(RESET_UNSTAGED_ALL);
                }
            }
            
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_RESET + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Reset to a specific commit (--mixed behavior: reset HEAD and index, keep working tree)
     */
    private int resetToCommit(String commitRef) throws IOException {
        String targetCommitHash = resolveCommitHash(commitRef);
        if (targetCommitHash == null) {
            return 1;
        }
        
        // Update current branch pointer to target commit
        String currentBranch = RepositoryState.getCurrentBranch();
        Path branchFile = HEADS.resolve(currentBranch);
        Files.writeString(branchFile, targetCommitHash + NEWLINE);
        
        // Reset index to match the target commit (--mixed behavior)
        Indexing.updateIndex(targetCommitHash);
        System.out.printf(RESET_HEAD_AT + NEWLINE, targetCommitHash.substring(0, HASH_SHORT_LENGTH));
        
        return 0;
    }
    
    /**
     * Resolve a commit reference (HEAD or HEAD~N) to an actual commit hash.
     * Prints error messages and returns null if resolution fails.
     */
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
        
        // Walk back N commits from current commit
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
    
    /**
     * Find files to unstage based on the provided patterns.
     * Validates that patterns match at least something in the working directory.
     * 
     * @return Set of file paths to unstage
     * @throws IOException if pattern doesn't match any files
     */
    private Set<String> findFilesToUnstage() throws IOException {
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        Map<String, String> allWorkingFiles = RepositoryState.getWorkingFiles();
        Set<String> filesToUnstage = new HashSet<>();
        
        for (String pattern : args) {
            Set<String> matchingIndexFiles = FileUtils.findMatchingFiles(pattern, indexMap);
            
            if (!matchingIndexFiles.isEmpty()) {
                filesToUnstage.addAll(matchingIndexFiles);
            } else {
                Set<String> matchingWorkingFiles = FileUtils.findMatchingFiles(pattern, allWorkingFiles.keySet());
                if (matchingWorkingFiles.isEmpty()) {
                    throw new IOException(String.format(ERROR_PATHSPEC_NO_MATCH, pattern));
                }
                // If matched working files but they weren't staged, silently continue
            }
        }
        
        return filesToUnstage;
    }
}
