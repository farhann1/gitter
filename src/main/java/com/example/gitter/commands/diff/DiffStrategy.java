package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.models.FileEntry;
import com.example.gitter.utils.FileUtils;
import com.example.gitter.utils.HashUtils;
import com.example.gitter.utils.Indexing;
import com.example.gitter.utils.ObjectStore;
import com.example.gitter.utils.OutputFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.GITTER;

public class DiffStrategy implements CommandStrategy<DiffOptions> {
    
    @Override
    public Integer execute(DiffOptions options) throws IOException {
        String normalizedFile = FileUtils.normalizePattern(options.getFile());
        Map<String, FileEntry> indexMap = Indexing.loadIndex();
        FileEntry indexEntry = indexMap.get(normalizedFile);
        
        if (indexEntry == null) {
            System.err.println(String.format(ERROR_PATHSPEC_NO_MATCH, options.getFile()));
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
    }
}
