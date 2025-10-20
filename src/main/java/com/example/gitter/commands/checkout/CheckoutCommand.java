package com.example.gitter.commands.checkout;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "checkout",
         synopsisHeading = "",
         customSynopsis = {
             "NAME:",
             "  checkout - Switch branch and restore working tree to match it",
             "",
             "SYNOPSIS:",
             "  gitter checkout [-b] <branch>",
             ""
         },
         descriptionHeading = "DESCRIPTION:%n",
         description = {
             "  Switch branch and restore working tree to match it",
             "",
             "  Switches to the specified branch and updates all files in the working tree",
             "  to match that branch. Requires a clean working directory to prevent data loss.",
             ""
         },
         optionListHeading = "OPTIONS:%n",
         parameterListHeading = "ARGUMENTS:%n"
)
public class CheckoutCommand implements Callable<Integer> {
    
    @Option(names = {"-b"},
            description = "Create a new branch and check it out")
    private boolean createBranch;
    
    @Parameters(paramLabel = "<branch>",
                description = "Branch to checkout",
                arity = "1")
    private String branch;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            CheckoutOptions options = CheckoutOptions.builder()
                    .branch(branch)
                    .createBranch(createBranch)
                    .build();
            
            CommandStrategy<CheckoutOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_CHECKOUT + e.getMessage());
            return 1;
        }
    }
}
