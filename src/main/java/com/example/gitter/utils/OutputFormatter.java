package com.example.gitter.utils;

import com.example.gitter.models.Commit;
import com.example.gitter.models.WorkingDirectoryStatus;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import java.io.IOException;
import java.time.Instant;

import static com.example.gitter.constants.Constants.*;
import static com.example.gitter.constants.Messages.*;

/**
 * Utility class for formatting and displaying output to the console.
 * Centralizes all UI/display logic for commands like diff, log, status, etc.
 */
public class OutputFormatter {
    
    /**
     * Show diff for a deleted file
     */
    public static void showDeletedFileDiff(String relativePath, String indexHash) throws IOException {
        String indexContent = ObjectStore.readBlob(indexHash).getDataAsString();
        System.out.println(String.format(DIFF_HEADER_A, relativePath));
        System.out.println(String.format(DIFF_HEADER_B, relativePath));
        showDiff(indexContent, EMPTY_STRING);
        System.out.println();
    }
    
    /**
     * Show diff between two file versions using unified diff format
     * 
     * @param oldContent The original content
     * @param newContent The new content
     */
    public static void showDiff(String oldContent, String newContent) {
        try {
            RawText oldText = new RawText(oldContent.getBytes());
            RawText newText = new RawText(newContent.getBytes());
            
            EditList edits = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.HISTOGRAM)
                    .diff(RawTextComparator.DEFAULT, oldText, newText);
            
            for (Edit edit : edits) {
                int hunkStartA = Math.max(0, edit.getBeginA() - DIFF_CONTEXT_LINES);
                int hunkEndA = Math.min(oldText.size(), edit.getEndA() + DIFF_CONTEXT_LINES);
                int hunkStartB = Math.max(0, edit.getBeginB() - DIFF_CONTEXT_LINES);
                int hunkEndB = Math.min(newText.size(), edit.getEndB() + DIFF_CONTEXT_LINES);
                
                int contextBeforeA = edit.getBeginA() - hunkStartA;
                int contextAfterA = hunkEndA - edit.getEndA();
                int contextBeforeB = edit.getBeginB() - hunkStartB;
                int contextAfterB = hunkEndB - edit.getEndB();
                
                // Print hunk header with actual line numbers and counts
                int oldCount = contextBeforeA + (edit.getEndA() - edit.getBeginA()) + contextAfterA;
                int newCount = contextBeforeB + (edit.getEndB() - edit.getBeginB()) + contextAfterB;
                
                System.out.println();
                System.out.println(COLOR_CYAN + String.format(DIFF_HUNK_HEADER,
                                 hunkStartA + 1, oldCount,
                                 hunkStartB + 1, newCount) + COLOR_RESET);
                
                // Print context before the change (from new text for consistency)
                for (int i = hunkStartB; i < edit.getBeginB(); i++) {
                    System.out.println(DIFF_CONTEXT_PREFIX + newText.getString(i));
                }
                
                // Print deletions
                for (int i = edit.getBeginA(); i < edit.getEndA(); i++) {
                    System.out.println(COLOR_RED + DIFF_DELETION_PREFIX + oldText.getString(i) + COLOR_RESET);
                }
                
                // Print insertions
                for (int i = edit.getBeginB(); i < edit.getEndB(); i++) {
                    System.out.println(COLOR_GREEN + DIFF_ADDITION_PREFIX + newText.getString(i) + COLOR_RESET);
                }
                
                // Print context after the change (from new text for consistency)
                for (int i = edit.getEndB(); i < hunkEndB; i++) {
                    System.out.println(DIFF_CONTEXT_PREFIX + newText.getString(i));
                }
            }
            
        } catch (Exception e) {
            System.err.println(ERROR_DIFF_GENERATION + e.getMessage());
        }
    }
    
    /**
     * Display a commit in log format
     */
    public static void displayCommit(Commit commit) {
        System.out.print(String.format(LOG_COMMIT_HASH, commit.getHash()) + NEWLINE);
        System.out.print(LOG_AUTHOR + NEWLINE);
        
        try {
            Instant instant = Instant.parse(commit.getTimestamp());
            String formattedDate = LOG_DATE_FORMATTER.format(instant);
            System.out.print(String.format(LOG_DATE, formattedDate) + NEWLINE);
        } catch (Exception e) {
            System.out.print(String.format(LOG_DATE, commit.getTimestamp()) + NEWLINE);
        }
        
        System.out.println();
        String[] messageLines = commit.getMessage().split(NEWLINE);
        for (String line : messageLines) {
            System.out.println(LOG_MESSAGE_INDENT + line);
        }
        
        System.out.println();
    }
    
    /**
     * Display status information
     */
    public static void displayStatus(String currentBranch, WorkingDirectoryStatus status) {
        System.out.printf(STATUS_ON_BRANCH + NEWLINE, currentBranch);
        
        if (status.isClean()) {
            System.out.println(STATUS_CLEAN);
        } else {
            displayStagedChanges(status);
            displayUnstagedChanges(status);
            displayUntrackedFiles(status);
        }
    }
    
    /**
     * Display staged changes (files ready to be committed)
     */
    private static void displayStagedChanges(WorkingDirectoryStatus status) {
        if (!status.hasStagedChanges()) {
            return;
        }
        
        System.out.println(STATUS_CHANGES_TO_COMMIT);
        System.out.println(HINT_RESET_TO_UNSTAGE);
        System.out.println();
        
        for (String file : status.getStagedNew()) {
            System.out.printf(STATUS_NEW_FILE + NEWLINE, file);
        }
        for (String file : status.getStagedModified()) {
            System.out.printf(STATUS_MODIFIED + NEWLINE, file);
        }
        for (String file : status.getStagedDeleted()) {
            System.out.printf(STATUS_DELETED + NEWLINE, file);
        }
    }
    
    /**
     * Display unstaged changes (modifications and deletions not yet staged)
     */
    private static void displayUnstagedChanges(WorkingDirectoryStatus status) {
        if (!status.hasUnstagedChanges()) {
            return;
        }
        
        System.out.println(STATUS_CHANGES_NOT_STAGED);
        System.out.println(HINT_ADD_FILES);
        System.out.println();
        
        for (String file : status.getUnstagedModified()) {
            System.out.printf(STATUS_MODIFIED_UNSTAGED + NEWLINE, file);
        }
        for (String file : status.getUnstagedDeleted()) {
            System.out.printf(STATUS_DELETED + NEWLINE, file);
        }
    }
    
    /**
     * Display untracked files (files not in version control)
     */
    private static void displayUntrackedFiles(WorkingDirectoryStatus status) {
        if (status.getUntracked().isEmpty()) {
            return;
        }
        
        System.out.println(STATUS_UNTRACKED_FILES);
        System.out.println(HINT_ADD_FILES);
        System.out.println();
        
        for (String file : status.getUntracked()) {
            System.out.printf(STATUS_UNTRACKED + NEWLINE, file);
        }
    }
}
