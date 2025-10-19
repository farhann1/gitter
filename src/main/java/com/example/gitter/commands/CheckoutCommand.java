package com.example.gitter.commands;

import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.*;

@Command(name = "checkout",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter checkout [-b] <branch>"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Switch branches or restore working tree files.",
             "",
             "  Updates files in the working tree to match the version in the index",
             "  or the specified tree. If no pathspec was given, gitter checkout will",
             "  also update HEAD to set the specified branch as the current branch."
         },
         optionListHeading = "%nOPTIONS%n",
         parameterListHeading = "%nARGUMENTS%n"
)
public class CheckoutCommand implements Callable<Integer> {
    
    @Option(names = {"-b"},
            description = "Create a new branch and check it out")
    private boolean createBranch;
    
    @Parameters(paramLabel = "<branch>",
                description = "Branch to checkout",
                arity = "1")
    private String branch;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            Path branchFile = HEADS.resolve(branch);
            
            if (createBranch) {
                return handleBranchCreation(branchFile);
            }
            
            if (!Files.exists(branchFile)) {
                System.err.print(String.format(ERROR_BRANCH_NOT_FOUND, branch) + NEWLINE);
                return 1;
            }
            
            String currentBranch = RepositoryState.getCurrentBranch();
            if (currentBranch.equals(branch)) {
                System.out.print(String.format(CHECKOUT_ALREADY_ON, branch) + NEWLINE);
                return 0;
            }
            
            if (!canSwitch()) {
                System.err.println(ERROR_UNCOMMITTED_CHANGES);
                System.err.println(ERROR_COMMIT_BEFORE_SWITCH);
                return 1;
            }
            
            String sourceCommit = RepositoryState.getCurrentCommitHash();
            String targetCommit = RepositoryState.getCommitHashFromBranch(branch);
            Files.writeString(HEAD, branch + NEWLINE);
            
            Indexing.updateIndex(targetCommit);
            RepositoryState.restoreWorkingTree(sourceCommit, targetCommit);
            
            System.out.print(String.format(CHECKOUT_SWITCHED, branch) + NEWLINE);
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_CHECKOUT + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Handle branch creation with -b option
     */
    private int handleBranchCreation(Path branchFile) throws IOException {
        if (Files.exists(branchFile)) {
            System.err.print(String.format(ERROR_BRANCH_EXISTS, branch) + NEWLINE);
            return 1;
        }
        
        // Create new branch file from current commit and point HEAD to it
        String currentCommit = RepositoryState.getCurrentCommitHash();
        Files.writeString(branchFile, currentCommit + NEWLINE);
        Files.writeString(HEAD, branch + NEWLINE);
        
        System.out.print(String.format(CHECKOUT_SWITCHED_NEW, branch) + NEWLINE);
        return 0;
    }
    
    /**
     * Blocks branch switch if ANY changes exist: staged, unstaged, or untracked
     * 
     * Future enhancement: Could match Git's behavior by only blocking
     * if local changes would conflict with target branch files.
     * 
     * @return true if working directory is completely clean, false otherwise
     */
    private boolean canSwitch() throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        return status.isClean();
    }
}
