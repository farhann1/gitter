package com.example.gitter.commands.commit;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "commit",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter commit -m <msg>",
             "  gitter commit -am <msg>"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Create a new commit containing the current contents of the index",
             "  with the given log message describing the changes.",
             "",
             "  The commit message should describe what changes are being made and why.",
             "",
             "  With -a flag, automatically stages modified and deleted tracked files",
             "  before committing (does not stage new untracked files)."
         },
         optionListHeading = "%nOPTIONS%n",
         sortOptions = false
)
public class CommitCommand implements Callable<Integer> {
    
    @Option(names = {"-m"}, 
            description = "Use the given <msg> as the commit message. Multiple -m options create separate paragraphs.",
            paramLabel = "<msg>",
            required = true)
    private String[] messages;
    
    @Option(names = {"-a"},
            description = "Automatically stage modified and deleted files before committing (does not add new files)")
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
