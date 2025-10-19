package com.example.gitter.commands.init;

import com.example.gitter.commands.strategy.CommandStrategy;
import com.example.gitter.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.*;

public class InitStrategy implements CommandStrategy<InitOptions> {
    
    @Override
    public Integer execute(InitOptions options) throws IOException {
        String currentDir = System.getProperty(USER_DIR);
        
        if (FileUtils.isGitterInitialized()) {
            String repoRoot = GITTER.getParent().toString();
            System.out.print(String.format(INIT_ALREADY_EXISTS, repoRoot) + NEWLINE);
            return 0;
        }
        
        try {
            createRepositoryStructure();
            System.out.print(String.format(INIT_SUCCESS, currentDir) + NEWLINE);
            return 0;
        } catch (IOException e) {
            System.err.print(String.format(ERROR_FAILED_TO_INIT, currentDir) + NEWLINE);
            return 1;
        }
    }
    
    private void createRepositoryStructure() throws IOException {
        Files.createDirectories(GITTER);
        Files.createDirectories(OBJECTS);
        Files.createDirectories(REFS);
        Files.createDirectories(HEADS);
        
        Path defaultBranchPath = HEADS.resolve(DEFAULT_BRANCH);
        Files.createFile(defaultBranchPath);
        Files.writeString(HEAD, DEFAULT_BRANCH + NEWLINE);
        Files.createFile(INDEX);
    }
}
