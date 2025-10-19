package com.example.gitter.commands.status;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "status",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter status"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Displays paths that have differences between the index file and the",
             "  current HEAD commit, paths that have differences between the working",
             "  tree and the index file, and paths in the working tree that are not",
             "  tracked by Gitter.",
             "",
             "  The output is categorized into three sections:",
             "",
             "    1. Changes to be committed (staged for next commit)",
             "    2. Changes not staged for commit (modified but not added)",  
             "    3. Untracked files (not in index or any commit)"
         }
)
public class StatusCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            StatusOptions options = StatusOptions.builder().build();
            CommandStrategy<StatusOptions> strategy = options.getStrategy();
            return strategy.execute(options);
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_READ + e.getMessage());
            return 1;
        }
    }
}
