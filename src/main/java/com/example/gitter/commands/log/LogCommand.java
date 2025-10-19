package com.example.gitter.commands.log;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "log",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter log"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Shows the commit logs.",
             "",
             "  List commits that are reachable by following the parent links from",
             "  the current branch HEAD, in reverse chronological order (newest first).",
             "",
             "  Displays up to 10 most recent commits. For each commit, the output shows:",
             "    - Commit hash (40-character SHA-1)",
             "    - Author information",
             "    - Commit date and time",
             "    - Commit message"
         }
)
public class LogCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            LogOptions options = LogOptions.builder().build();
            CommandStrategy<LogOptions> strategy = options.getStrategy();
            return strategy.execute(options);
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_READ_LOG + e.getMessage());
            return 1;
        }
    }
}
