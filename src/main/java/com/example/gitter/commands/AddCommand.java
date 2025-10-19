package com.example.gitter.commands;

import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.NEWLINE;
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
            WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
            Map<String, FileEntry> indexMap = Indexing.loadIndex();
            
            Set<String> changedAndNewFiles = new HashSet<>();
            changedAndNewFiles.addAll(status.getUnstagedModified());
            changedAndNewFiles.addAll(status.getUntracked());
            
            for (String pattern : files) {
                processPattern(pattern, changedAndNewFiles, status.getUnstagedDeleted(), status.getAllWorkingFiles(), indexMap);
            }
            
            Indexing.saveIndex(indexMap.values());
            System.out.print(ADD_SUCCESS + NEWLINE);
            return 0;
            
        } catch (IOException e) {
            System.err.println(ERROR_FAILED_TO_ADD + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Process a single pattern and stage matching files.
     * 
     * Processing logic:
     * 1. Find and stage all matching changed/new files
     * 2. Find and stage all matching deletions
     * 3. If nothing matched, check if pattern matches existing unchanged files (silently succeed)
     * 4. If nothing matched at all, throw error (invalid pattern/typo)
     * 
     * @param pattern User-provided pattern (e.g., "file.txt", "*.java", "src/", ".")
     * @param changedAndNewFiles Set of files that can be added (unstaged modified + untracked)
     * @param deletedFiles Set of files that have been deleted (unstaged deletions)
     * @param allWorkingFiles Map of all files in working directory (for validation)
     * @param indexMap Map of staged files (to update)
     * @throws IOException if pattern doesn't match any files (invalid pattern/typo)
     */
    private void processPattern(String pattern, 
                                Set<String> changedAndNewFiles,
                                Set<String> deletedFiles,
                                Map<String, String> allWorkingFiles,
                                Map<String, FileEntry> indexMap) throws IOException {
        
        Set<String> matchingChangedFiles = FileUtils.findMatchingFiles(pattern, changedAndNewFiles);
        Indexing.stageFiles(matchingChangedFiles, indexMap);

        Set<String> matchingDeletedFiles = FileUtils.findMatchingFiles(pattern, deletedFiles);
        for (String filePath : matchingDeletedFiles) {
            indexMap.remove(filePath);
        }
        
        if (matchingChangedFiles.isEmpty() && matchingDeletedFiles.isEmpty()) {
            Set<String> matchingExistingFiles = FileUtils.findMatchingFiles(pattern, allWorkingFiles.keySet());
            if (matchingExistingFiles.isEmpty()) {
                throw new IOException(String.format(ERROR_PATHSPEC_NO_MATCH, pattern));
            }
        }
    }
}
