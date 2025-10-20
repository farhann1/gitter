package com.example.gitter.commands.init;

import com.example.gitter.commands.strategy.CommandStrategy;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.ERROR_INIT_EXCEPTION;

@Command(name = "init",
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "  init - Creates an empty Gitter repository",
             "",
             "SYNOPSIS:",
             "  gitter init",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "  Creates an empty Gitter repository",
             "",
             "  Initializes a new Gitter repository by creating a .gitter directory structure",
             "  with subdirectories for objects, refs, and files for HEAD and index.",
             ""
         }
)
public class InitCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        try {
            InitOptions options = InitOptions.builder().build();
            CommandStrategy<InitOptions> strategy = options.getStrategy();
            return strategy.execute(options);
        } catch (Exception e) {
            System.err.println(ERROR_INIT_EXCEPTION + e.getMessage());
            return 1;
        }
    }
}
