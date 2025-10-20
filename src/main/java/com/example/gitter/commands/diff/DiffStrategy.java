package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.FileEntry;
import com.example.gitter.models.WorkingDirectoryStatus;
import com.example.gitter.utils.HashUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.OutputFormatter;
import com.example.gitter.utils.RepositoryState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.GITTER;

public class DiffStrategy implements CommandStrategy<DiffOptions> {
    
    @Override
    public Integer execute(DiffOptions options) throws IOException {
        WorkingDirectoryStatus status = RepositoryState.getWorkingDirectoryStatus();
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        Path workingDir = GITTER.getParent();
        
        Set<String> modifiedFiles = status.getUnstagedModified();
        Set<String> deletedFiles = status.getUnstagedDeleted();
        
        if (modifiedFiles.isEmpty() && deletedFiles.isEmpty()) {
            return 0;
        }
        
        // Show diffs for modified files
        for (String file : modifiedFiles) {
            showFileDiff(file, indexMap, workingDir);
        }
        
        // Show diffs for deleted files
        for (String file : deletedFiles) {
            FileEntry indexEntry = indexMap.get(file);
            if (indexEntry != null) {
                OutputFormatter.showDeletedFileDiff(file, indexEntry.getHash());
            }
        }
        
        return 0;
    }
    
    private void showFileDiff(String file, Map<String, FileEntry> indexMap, Path workingDir) throws IOException {
        FileEntry indexEntry = indexMap.get(file);
        if (indexEntry == null) {
            return;
        }
        
        Path workingFile = workingDir.resolve(file);
        if (!Files.exists(workingFile)) {
            return;
        }
        
        String workingHash = HashUtils.hashFile(workingFile);
        if (indexEntry.getHash().equals(workingHash)) {
            return;
        }
        
        String indexContent = ObjectStore.readBlob(indexEntry.getHash()).getDataAsString();
        String workingContent = Files.readString(workingFile);
        
        System.out.println(String.format(DIFF_HEADER_A, file));
        System.out.println(String.format(DIFF_HEADER_B, file));
        OutputFormatter.showDiff(indexContent, workingContent);
        System.out.println();
    }
}
