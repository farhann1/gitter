package com.example.gitter.commands;

import com.example.gitter.models.FileEntry;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.HashUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.OutputFormatter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.*;

/**
 * Shows diff for a single file between working directory and index.
 * 
 * NOTE: Future enhancements to consider:
 * - Support multiple files: gitter diff file1.txt file2.txt
 * - Support glob patterns: gitter diff *.java src/*.txt
 * - Support directory: gitter diff src/
 * - Show all changes: gitter diff (no args shows all modified files)
 */
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
             "        Glob patterns (*.txt) and directories (src/) are not supported."
         }
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
            String normalizedFile = FileUtils.normalizePattern(file);
            Map<String, FileEntry> indexMap = Indexing.loadIndex();
            FileEntry indexEntry = indexMap.get(normalizedFile);
            
            if (indexEntry == null) {
                System.err.println(String.format(ERROR_PATHSPEC_NO_MATCH, file));
                return 1;
            }
            
            Path workingDir = GITTER.getParent();
            Path workingFile = workingDir.resolve(normalizedFile);
            
            if (!Files.exists(workingFile)) {
                OutputFormatter.showDeletedFileDiff(normalizedFile, indexEntry.getHash());
                return 0;
            }
            
            String workingHash = HashUtils.hashFile(workingFile);
            if (indexEntry.getHash().equals(workingHash)) {
                System.out.print(DIFF_NO_CHANGES + NEWLINE);
                return 0;
            }
            
            String indexContent = ObjectStore.readBlob(indexEntry.getHash()).getDataAsString();
            String workingContent = Files.readString(workingFile);
            
            System.out.println(COLOR_BOLD + String.format(DIFF_HEADER, normalizedFile, normalizedFile) + COLOR_RESET);
            OutputFormatter.showDiff(indexContent, workingContent);
            
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_DIFF + e.getMessage());
            return 1;
        }
    }
}
