package com.example.gitter.commands;

import com.example.gitter.utils.FileUtils;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static com.example.gitter.constants.Constants.DEFAULT_BRANCH;
import static com.example.gitter.constants.Constants.NEWLINE;
import static com.example.gitter.constants.Constants.USER_DIR;
import static com.example.gitter.constants.Messages.*;
import static com.example.gitter.constants.PathConstants.GITTER;
import static com.example.gitter.constants.PathConstants.HEAD;
import static com.example.gitter.constants.PathConstants.HEADS;
import static com.example.gitter.constants.PathConstants.INDEX;
import static com.example.gitter.constants.PathConstants.OBJECTS;
import static com.example.gitter.constants.PathConstants.REFS;

/**
 * Initializes a new Gitter repository with directory structure as follows:
 * 
 * .gitter/
 * ├── objects/          # Content-addressable object store (blobs, commits, trees)
 * │   ├── ab/           # Sharded by first 2 chars of hash
 * │   │   └── cdef123... # Object file: "type size\0content"
 * │   └── ...
 * ├── refs/
 * │   └── heads/        # Branch pointers (files containing commit hashes)
 * │       ├── main
 * │       └── feature
 * ├── HEAD              # Current branch reference (contains branch name)
 * └── index             # Staging area (tracks staged files)
 * 
 */
@Command(name = "init",
         synopsisHeading = "%nUSAGE%n",
         customSynopsis = {
             "  gitter init"
         },
         descriptionHeading = "%nDESCRIPTION%n",
         description = {
             "  This command creates an empty Gitter repository using Git's",
             "  content-addressable object storage architecture.",
             "",
             "  The .gitter directory contains:",
             "  - objects/: Unified storage for all version-controlled content",
             "  - refs/heads/: Branch pointers",
             "  - HEAD: Current branch reference",
             "  - index: Staging area",
             "",
             "  Running gitter init in an existing repository is safe. It will not",
             "  overwrite things that are already there."
         }
)
public class InitCommand implements Callable<Integer> {

    public Integer call() {
        String currentDir = System.getProperty(USER_DIR);

        if(FileUtils.isGitterInitialized()) {
            String repoRoot = GITTER.getParent().toString();
            System.out.print(String.format(INIT_ALREADY_EXISTS, repoRoot) + NEWLINE);
            return 0;
        }

        try {
            Files.createDirectories(GITTER);
            Files.createDirectories(OBJECTS);
            Files.createDirectories(REFS);
            Files.createDirectories(HEADS);

            Path defaultBranchPath = HEADS.resolve(DEFAULT_BRANCH);
            
            Files.createFile(defaultBranchPath);
            Files.writeString(HEAD, DEFAULT_BRANCH + NEWLINE);
            Files.createFile(INDEX);

            System.out.print(String.format(INIT_SUCCESS, currentDir) + NEWLINE);
        } catch(IOException exception) {
            System.err.print(String.format(ERROR_FAILED_TO_INIT, currentDir) + NEWLINE);
            return 1;
        }

        return 0;
    }
}
