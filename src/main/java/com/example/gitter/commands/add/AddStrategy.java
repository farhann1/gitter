package com.example.gitter.commands.add;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.gitter.constants.Messages.ERROR_PATHSPEC_NO_MATCH;

public class AddStrategy implements CommandStrategy<AddOptions> {
    
    @Override
    public Integer execute(AddOptions options) throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        
        Set<String> changedAndNewFiles = new HashSet<>();
        changedAndNewFiles.addAll(status.getUnstagedModified());
        changedAndNewFiles.addAll(status.getUntracked());
        
        for (String pattern : options.getFiles()) {
            processPattern(pattern, changedAndNewFiles, status.getUnstagedDeleted(), 
                          status.getAllWorkingFiles().keySet(), indexMap);
        }
        
        Indexing.saveIndex(indexMap.values());
        return 0;
    }
    
    private void processPattern(String pattern, 
                                Set<String> changedAndNewFiles,
                                Set<String> deletedFiles,
                                Set<String> allWorkingFiles,
                                Map<String, FileEntry> indexMap) throws IOException {
        
        Set<String> matchingChangedFiles = FileUtils.findMatchingFiles(pattern, changedAndNewFiles);
        Indexing.stageFiles(matchingChangedFiles, indexMap);
        
        Set<String> matchingDeletedFiles = FileUtils.findMatchingFiles(pattern, deletedFiles);
        for (String filePath : matchingDeletedFiles) {
            indexMap.remove(filePath);
        }
        
        if (matchingChangedFiles.isEmpty() && matchingDeletedFiles.isEmpty()) {
            Set<String> matchingExistingFiles = FileUtils.findMatchingFiles(pattern, allWorkingFiles);
            if (matchingExistingFiles.isEmpty()) {
                throw new IOException(String.format(ERROR_PATHSPEC_NO_MATCH, pattern));
            }
        }
    }
}
