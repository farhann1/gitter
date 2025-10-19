package com.example.gitter.commands.reset;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

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
            ResetOptions options = ResetOptions.builder()
                    .args(args)
                    .build();
            
            CommandStrategy<ResetOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_RESET + e.getMessage());
            return 1;
        }
    }
}
