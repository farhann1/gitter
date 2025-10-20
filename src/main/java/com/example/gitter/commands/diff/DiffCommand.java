package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.example.gitter.constants.Messages.*;

@Command(name = "diff",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter diff <file>"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  Show changes between the working tree and the index for a file.",
             "",
             "  The output uses unified diff format with color coding:",
             "    - Red lines (prefixed with -) indicate deletions",
             "    - Green lines (prefixed with +) indicate additions",
             "    - Cyan lines show hunk headers (@@ line numbers)",
             "",
             "  NOTE: Only exact file paths are supported.",
             "        Glob patterns (*.txt) and directories (src/) are not supported yet."
         },
         parameterListHeading = "%nARGUMENTS%n"
)
public class DiffCommand implements Callable<Integer> {
    
    @Parameters(index = "0",
                paramLabel = "<file>",
                description = "File to show diff for")
    private String file;
    
    @Override
    public Integer call() {
        if (!FileUtils.isGitterInitialized()) {
            System.err.println(ERROR_NOT_INITIALIZED);
            return 1;
        }
        
        try {
            DiffOptions options = DiffOptions.builder()
                    .file(file)
                    .build();
            
            CommandStrategy<DiffOptions> strategy = options.getStrategy();
            return strategy.execute(options);
            
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println(ERROR_FAILED_TO_DIFF + e.getMessage());
            return 1;
        }
    }
}
