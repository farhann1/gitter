package com.example.gitter.commands.commit;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "commit",
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "commit - Record changes to the repository",
             "",
             "SYNOPSIS:",
             "gitter commit -m <msg> [-a]",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "Record changes to the repository",
             "",
             "Creates a snapshot of staged changes with the provided commit message.",
             "Each commit represents a point in the project history.",
             ""
         },
         optionListHeading = "OPTIONS:%n",
         sortOptions = false
)
public class CommitCommand implements Callable<Integer> {
    
    @Option(names = {"-m"}, 
            description = "Commit message (can be specified multiple times)",
            paramLabel = "<msg>",
            required = true)
    private String[] messages;
    
    @Option(names = {"-a"},
            description = "Automatically stage modified and deleted files (excludes new files)")
    private boolean stageAll = false;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            CommitOptions options = CommitOptions.builder()
                    .messages(messages)
                    .stageAll(stageAll)
                    .build();
            
            CommandStrategy<CommitOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_COMMIT + e.getMessage());
            return 1;
        }
    }
}
