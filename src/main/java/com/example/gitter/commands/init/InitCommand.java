package com.example.gitter.commands.init;

import com.example.gitter.commands.strategy.CommandStrategy;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.ERROR_INIT_EXCEPTION;

@Command(name = "init",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter init"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  This command creates an empty Gitter repository using Git's",
             "  content-addressable object storage architecture.",
             "",
             "  The .gitter directory contains:",
             "  - objects/: Unified storage for all version-controlled content",
             "  - refs/heads/: Branch pointers",
             "  - HEAD: Current branch reference",
             "  - index: Staging area",
             "",
             "  Running gitter init in an existing repository is safe. It will not",
             "  overwrite things that are already there."
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
