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
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "  reset - Reset to a specific commit or unstage files",
             "",
             "SYNOPSIS:",
             "  gitter reset [<commit|file>...]",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "Reset to a specific commit or unstage files",
             "",
             "Resets the current branch HEAD to a specified commit and clears the staging",
             "area (working tree unchanged), or removes specified files from the staging area.",
             "",
             "Examples:",
             "gitter reset HEAD~1      # Undo last commit",
             "gitter reset HEAD~2      # Undo last 2 commits",
             "gitter reset             # Unstage all changes",
             "gitter reset file.txt    # Unstage specific file",
             "gitter reset '*.txt'     # Unstage using file pattern (use quote)",
             ""
         },
         parameterListHeading = "ARGUMENTS:%n"
)
public class ResetCommand implements Callable<Integer> {
    
    @Parameters(paramLabel = "<commit|file>",
                description = "Commit or file pattern",
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
