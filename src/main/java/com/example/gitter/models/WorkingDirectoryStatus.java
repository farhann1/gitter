package com.example.gitter.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the complete status of the working directory and categorizes all files into: 
 * - staged (new/modified/deleted)
 * - unstaged (modified/deleted)
 * - untracked.
 */
public class WorkingDirectoryStatus {
    private final Set<String> stagedNew;
    private final Set<String> stagedModified;
    private final Set<String> stagedDeleted;
    private final Set<String> unstagedModified;
    private final Set<String> unstagedDeleted;
    private final Set<String> untracked;
    private final Map<String, String> allWorkingFiles;
    
    public WorkingDirectoryStatus(Map<String, String> allWorkingFiles) {
        this.stagedNew = new HashSet<>();
        this.stagedModified = new HashSet<>();
        this.stagedDeleted = new HashSet<>();
        this.unstagedModified = new HashSet<>();
        this.unstagedDeleted = new HashSet<>();
        this.untracked = new HashSet<>();
        this.allWorkingFiles = allWorkingFiles;
    }
    
    public Set<String> getStagedNew() {
        return Collections.unmodifiableSet(stagedNew);
    }
    
    public Set<String> getStagedModified() {
        return Collections.unmodifiableSet(stagedModified);
    }
    
    public Set<String> getStagedDeleted() {
        return Collections.unmodifiableSet(stagedDeleted);
    }
    
    public Set<String> getUnstagedModified() {
        return Collections.unmodifiableSet(unstagedModified);
    }
    
    public Set<String> getUnstagedDeleted() {
        return Collections.unmodifiableSet(unstagedDeleted);
    }
    
    public Set<String> getUntracked() {
        return Collections.unmodifiableSet(untracked);
    }
    
    public Map<String, String> getAllWorkingFiles() {
        return Collections.unmodifiableMap(allWorkingFiles);
    }
    
    public boolean hasStagedChanges() {
        return !stagedNew.isEmpty() || !stagedModified.isEmpty() || !stagedDeleted.isEmpty();
    }
    
    public boolean hasUnstagedChanges() {
        return !unstagedModified.isEmpty() || !unstagedDeleted.isEmpty();
    }
    
    public boolean isClean() {
        return !hasStagedChanges() && !hasUnstagedChanges() && untracked.isEmpty();
    }
    
    public void addStagedNew(String path) {
        stagedNew.add(path);
    }
    
    public void addStagedModified(String path) {
        stagedModified.add(path);
    }
    
    public void addStagedDeleted(String path) {
        stagedDeleted.add(path);
    }
    
    public void addUnstagedModified(String path) {
        unstagedModified.add(path);
    }
    
    public void addUnstagedDeleted(String path) {
        unstagedDeleted.add(path);
    }
    
    public void addUntracked(String path) {
        untracked.add(path);
    }
}
