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
             "  Shows the working tree status.",
             "",
             "  Displays changes in three categories:",
             "    • Changes staged for commit",
             "    • Changes not staged for commit",
             "    • Untracked files"
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
