package com.example.gitter.commands.reset;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.FileEntry;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.gitter.constants.Messages.ERROR_PATHSPEC_NO_MATCH;

public class UnstageFilesStrategy implements CommandStrategy<ResetOptions> {
    
    @Override
    public Integer execute(ResetOptions options) throws IOException {
        Set<String> filesToUnstage = findFilesToUnstage(options);
        
        if (!filesToUnstage.isEmpty()) {
            Indexing.unstageFiles(filesToUnstage);
        }
        
        return 0;
    }
    
    private Set<String> findFilesToUnstage(ResetOptions options) throws IOException {
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        
        if (options.isEmpty()) {
            return indexMap.keySet();
        }
        
        Map<String, String> allWorkingFiles = RepositoryState.getWorkingFiles();
        Set<String> filesToUnstage = new HashSet<>();
        
        for (String pattern : options.getArgs()) {
            Set<String> matchingIndexFiles = FileUtils.findMatchingFiles(pattern, indexMap);
            
            if (!matchingIndexFiles.isEmpty()) {
                filesToUnstage.addAll(matchingIndexFiles);
            } else {
                Set<String> matchingWorkingFiles = FileUtils.findMatchingFiles(pattern, allWorkingFiles.keySet());
                if (matchingWorkingFiles.isEmpty()) {
                    throw new IOException(String.format(ERROR_PATHSPEC_NO_MATCH, pattern));
                }
            }
        }
        
        return filesToUnstage;
    }
}
