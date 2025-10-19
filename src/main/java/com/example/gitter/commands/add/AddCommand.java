package com.example.gitter.commands.add;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "add",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter add <pathspec>..."
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  This command updates the index using the current content found in",
             "  the working tree, to prepare the content staged for the next commit.",
             "",
             "  The 'add' command can be performed multiple times before a commit.",
             "  It only adds the content of the specified file(s) at the time the",
             "  add command is run."
         },
         parameterListHeading = "%nARGUMENTS%n"
)
public class AddCommand implements Callable<Integer> {
    
    @Parameters(paramLabel = "<pathspec>", 
                description = "Files to add content from. Fileglobs (e.g. *.c) can be given to add all matching files.",
                arity = "1..*")
    private List<String> files;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            AddOptions options = AddOptions.builder()
                    .files(files)
                    .build();
            
            CommandStrategy<AddOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_ADD + e.getMessage());
            return 1;
        }
    }
}
