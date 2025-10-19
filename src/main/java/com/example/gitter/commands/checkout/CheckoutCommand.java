package com.example.gitter.commands.checkout;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "checkout",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter checkout [-b] <branch>"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Switch branches or restore working tree files.",
             "",
             "  Updates files in the working tree to match the version in the index",
             "  or the specified tree. If no pathspec was given, gitter checkout will",
             "  also update HEAD to set the specified branch as the current branch."
         },
         optionListHeading = "%nOPTIONS%n",
         parameterListHeading = "%nARGUMENTS%n"
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
