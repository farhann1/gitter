package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "diff",
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "  diff - Show changes between the working tree and the index",
             "",
             "SYNOPSIS:",
             "  gitter diff",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "Show changes between the working tree and the index",
             "",
             "Shows unstaged changes for all modified files in unified diff format.",
             "Compares the working tree version against what's staged in the index.",
             ""
         }
)
public class DiffCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            DiffOptions options = DiffOptions.builder().build();
            CommandStrategy<DiffOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_DIFF + e.getMessage());
            return 1;
        }
    }
}
