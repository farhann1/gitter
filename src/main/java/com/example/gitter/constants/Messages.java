package com.example.gitter.constants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Constants for console output messages used across commands
 * Centralizes all user-facing messages for consistency and easier maintenance
 */
public class Messages {
    
    // ANSI color codes
    public static final String COLOR_RESET = "\u001B[0m";
    public static final String COLOR_RED = "\u001B[31m";
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_CYAN = "\u001B[36m";
    public static final String COLOR_BOLD = "\u001B[1m";

    // Date formatting for log output
    public static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter
        .ofPattern("EEE MMM dd HH:mm:ss yyyy Z")
        .withZone(ZoneId.systemDefault());
    
    // Error messages
    public static final String ERROR_NOT_INITIALIZED = "Error: Not a gitter repository. Run 'gitter init' first.";
    public static final String ERROR_UNCOMMITTED_CHANGES = "Error: You have uncommitted changes.";
    public static final String ERROR_COMMIT_BEFORE_SWITCH = "Please commit your changes or reset before you switch branches.";
    public static final String ERROR_FAILED_TO_READ = "Error: Failed to read repository state - ";
    public static final String ERROR_FAILED_TO_CHECKOUT = "Error: Failed to checkout branch - ";
    public static final String ERROR_BRANCH_NOT_FOUND = "Error: pathspec '%s' did not match any file(s) known to gitter";
    public static final String ERROR_BRANCH_EXISTS = "Error: A branch named '%s' already exists.";
    public static final String ERROR_REPOSITORY_CORRUPT = "Error: Repository data is corrupted";
    
    // Status messages
    public static final String STATUS_ON_BRANCH = "On branch %s";
    public static final String STATUS_CLEAN = "\nNothing to commit, working tree clean";
    public static final String STATUS_CHANGES_TO_COMMIT = "\nChanges to be committed:";
    public static final String STATUS_CHANGES_NOT_STAGED = "\nChanges not staged for commit:";
    public static final String STATUS_UNTRACKED_FILES = "\nUntracked files:";
    
    // Status hints
    public static final String HINT_RESET_TO_UNSTAGE = "  (use \"gitter reset\" to unstage)";
    public static final String HINT_ADD_TO_UPDATE = "  (use \"gitter add <file>...\" to update what will be committed)";
    public static final String HINT_ADD_TO_TRACK = "  (use \"gitter add <file>...\" to include in what will be committed)";
    
    // Status file prefixes
    public static final String STATUS_NEW_FILE = "  " + COLOR_GREEN + "new file:   %s" + COLOR_RESET;
    public static final String STATUS_MODIFIED = "  " + COLOR_GREEN + "modified:   %s" + COLOR_RESET;
    public static final String STATUS_MODIFIED_UNSTAGED = "  " + COLOR_RED + "modified:   %s" + COLOR_RESET;
    public static final String STATUS_DELETED = "  " + COLOR_RED + "deleted:    %s" + COLOR_RESET;
    public static final String STATUS_UNTRACKED = "  " + COLOR_RED + "%s" + COLOR_RESET;
    
    // Checkout messages
    public static final String CHECKOUT_SWITCHED = "Switched to branch '%s'";
    public static final String CHECKOUT_SWITCHED_NEW = "Switched to a new branch '%s'";
    public static final String CHECKOUT_ALREADY_ON = "Already on '%s'";
    
    // Init messages
    public static final String INIT_SUCCESS = "Initialized empty Gitter repository in %s";
    public static final String INIT_ALREADY_EXISTS = "Gitter repository already exists at %s";
    public static final String ERROR_FAILED_TO_INIT = "Failed to initialize gitter repository in %s";
    
    // Log messages
    public static final String LOG_NO_COMMITS = "No commits yet.";
    public static final String LOG_COMMIT_HASH = COLOR_YELLOW + "commit %s" + COLOR_RESET;
    public static final String LOG_AUTHOR = "Author: user";
    public static final String LOG_DATE = "Date:   %s";
    public static final String LOG_MESSAGE_INDENT = "    ";
    public static final String ERROR_FAILED_TO_READ_LOG = "Error: Failed to read commit log - ";
    
    // Add command messages
    public static final String ADD_SUCCESS = "Files added to staging area.";
    public static final String ERROR_PATHSPEC_NO_MATCH = "Error: pathspec '%s' did not match any files";
    public static final String ERROR_FATAL_PATHSPEC_NO_MATCH = "fatal: pathspec '%s' did not match any files";
    public static final String ERROR_FAILED_TO_ADD = "Error: Failed to add files - ";
    
    // Commit command messages
    public static final String COMMIT_SUCCESS = "[%s %s] %s";
    public static final String COMMIT_FILES_CHANGED = "%d file(s) changed";
    public static final String ERROR_NOTHING_TO_COMMIT = "Error: Nothing to commit. Use 'gitter add' to stage files.";
    public static final String ERROR_FAILED_TO_COMMIT = "Error: Failed to create commit - ";

    // Diff command messages
    public static final String DIFF_NO_CHANGES = "No changes detected.";
    public static final String DIFF_HEADER = "diff --gitter a/%s b/%s";
    public static final String DIFF_DELETED_FILE = "deleted file";
    public static final String DIFF_HUNK_HEADER = "@@ -%d,%d +%d,%d @@";
    public static final String DIFF_DELETION_PREFIX = "- ";
    public static final String DIFF_ADDITION_PREFIX = "+ ";
    public static final String DIFF_CONTEXT_PREFIX = "  ";
    public static final String ERROR_FAILED_TO_DIFF = "Error: Failed to generate diff - ";
    public static final String ERROR_DIFF_GENERATION = "Error generating diff: ";
    
    // Reset command messages
    public static final String RESET_UNSTAGED_ALL = "Unstaged all changes.";
    public static final String RESET_NO_FILES_UNSTAGED = "No files were unstaged.";
    public static final String RESET_HEAD_AT = "HEAD is now at %s";
    public static final String ERROR_INVALID_COMMIT_REF = "Error: Invalid commit reference: %s";
    public static final String ERROR_NO_COMMITS_YET = "Error: No commits yet on branch %s";
    public static final String ERROR_CANNOT_GO_BACK_INITIAL = "Error: Cannot go back %d commits (reached initial commit)";
    public static final String ERROR_FAILED_TO_RESET = "Error: Failed to reset - ";
    
    // Hash utility messages
    public static final String ERROR_HASH_ALGORITHM_NOT_FOUND = "%s algorithm not found";
    
    // Object store messages
    public static final String ERROR_EXPECTED_OBJECT_TYPE = "Expected %s, got %s";
    public static final String ERROR_OBJECT_NOT_FOUND = "Object not found: %s";
    public static final String ERROR_OBJECT_SIZE_MISMATCH = "Object corruption: expected size %d, got %d";
    public static final String ERROR_MALFORMED_OBJECT_NO_NULL = "Malformed object: no null byte separator";
    public static final String ERROR_MALFORMED_OBJECT_HEADER = "Malformed object header: %s";
    
    // Repository state messages
    public static final String WARNING_COULD_NOT_PROCESS_FILE = "Warning: Could not process %s: %s";
}
