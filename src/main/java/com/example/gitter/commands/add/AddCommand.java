package com.example.gitter.commands.add;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "add",
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "  add - Stage content for the next commit",
             "",
             "SYNOPSIS:",
             "  gitter add <pathspec>...",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "  Stage content for the next commit",
             "",
             "  Adds file changes to the staging area, preparing them to be included in",
             "  the next commit. Can be run multiple times to incrementally stage changes.",
             ""
         },
         parameterListHeading = "ARGUMENTS:%n"
)
public class AddCommand implements Callable<Integer> {
    
    @Parameters(paramLabel = "<pathspec>", 
                description = "Files, directories, or patterns to stage",
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
