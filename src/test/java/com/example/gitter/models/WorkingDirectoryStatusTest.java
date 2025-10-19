package com.example.gitter.models;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkingDirectoryStatusTest {

    @Test
    void testEmptyStatusIsClean() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());

        assertTrue(status.isClean());
        assertFalse(status.hasStagedChanges());
        assertFalse(status.hasUnstagedChanges());
        assertTrue(status.getStagedNew().isEmpty());
        assertTrue(status.getStagedModified().isEmpty());
        assertTrue(status.getStagedDeleted().isEmpty());
        assertTrue(status.getUnstagedModified().isEmpty());
        assertTrue(status.getUnstagedDeleted().isEmpty());
        assertTrue(status.getUntracked().isEmpty());
    }

    @Test
    void testAddStagedNew() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedNew("new.txt");

        assertFalse(status.isClean());
        assertTrue(status.hasStagedChanges());
        assertFalse(status.hasUnstagedChanges());
        assertTrue(status.getStagedNew().contains("new.txt"));
        assertEquals(1, status.getStagedNew().size());
    }

    @Test
    void testAddStagedModified() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedModified("modified.txt");

        assertFalse(status.isClean());
        assertTrue(status.hasStagedChanges());
        assertFalse(status.hasUnstagedChanges());
        assertTrue(status.getStagedModified().contains("modified.txt"));
        assertEquals(1, status.getStagedModified().size());
    }

    @Test
    void testAddStagedDeleted() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedDeleted("deleted.txt");

        assertFalse(status.isClean());
        assertTrue(status.hasStagedChanges());
        assertFalse(status.hasUnstagedChanges());
        assertTrue(status.getStagedDeleted().contains("deleted.txt"));
        assertEquals(1, status.getStagedDeleted().size());
    }

    @Test
    void testAddUnstagedModified() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUnstagedModified("unstaged.txt");

        assertFalse(status.isClean());
        assertFalse(status.hasStagedChanges());
        assertTrue(status.hasUnstagedChanges());
        assertTrue(status.getUnstagedModified().contains("unstaged.txt"));
        assertEquals(1, status.getUnstagedModified().size());
    }

    @Test
    void testAddUnstagedDeleted() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUnstagedDeleted("deleted.txt");

        assertFalse(status.isClean());
        assertFalse(status.hasStagedChanges());
        assertTrue(status.hasUnstagedChanges());
        assertTrue(status.getUnstagedDeleted().contains("deleted.txt"));
        assertEquals(1, status.getUnstagedDeleted().size());
    }

    @Test
    void testAddUntracked() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUntracked("untracked.txt");

        assertFalse(status.isClean());
        assertFalse(status.hasStagedChanges());
        assertFalse(status.hasUnstagedChanges());
        assertTrue(status.getUntracked().contains("untracked.txt"));
        assertEquals(1, status.getUntracked().size());
    }

    @Test
    void testMultipleStagedChanges() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedNew("new1.txt");
        status.addStagedNew("new2.txt");
        status.addStagedModified("modified.txt");
        status.addStagedDeleted("deleted.txt");

        assertTrue(status.hasStagedChanges());
        assertEquals(2, status.getStagedNew().size());
        assertEquals(1, status.getStagedModified().size());
        assertEquals(1, status.getStagedDeleted().size());
    }

    @Test
    void testMultipleUnstagedChanges() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUnstagedModified("modified1.txt");
        status.addUnstagedModified("modified2.txt");
        status.addUnstagedDeleted("deleted.txt");

        assertTrue(status.hasUnstagedChanges());
        assertEquals(2, status.getUnstagedModified().size());
        assertEquals(1, status.getUnstagedDeleted().size());
    }

    @Test
    void testMixedChanges() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedNew("staged-new.txt");
        status.addUnstagedModified("unstaged-mod.txt");
        status.addUntracked("untracked.txt");

        assertFalse(status.isClean());
        assertTrue(status.hasStagedChanges());
        assertTrue(status.hasUnstagedChanges());
        assertEquals(1, status.getStagedNew().size());
        assertEquals(1, status.getUnstagedModified().size());
        assertEquals(1, status.getUntracked().size());
    }

    @Test
    void testGetAllWorkingFiles() {
        Map<String, String> workingFiles = new HashMap<>();
        workingFiles.put("file1.txt", "hash1");
        workingFiles.put("file2.txt", "hash2");

        WorkingDirectoryStatus status = new WorkingDirectoryStatus(workingFiles);

        assertEquals(2, status.getAllWorkingFiles().size());
        assertEquals("hash1", status.getAllWorkingFiles().get("file1.txt"));
        assertEquals("hash2", status.getAllWorkingFiles().get("file2.txt"));
    }

    @Test
    void testImmutabilityOfStagedNew() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedNew("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getStagedNew().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfStagedModified() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedModified("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getStagedModified().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfStagedDeleted() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addStagedDeleted("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getStagedDeleted().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfUnstagedModified() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUnstagedModified("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getUnstagedModified().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfUnstagedDeleted() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUnstagedDeleted("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getUnstagedDeleted().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfUntracked() {
        WorkingDirectoryStatus status = new WorkingDirectoryStatus(Map.of());
        status.addUntracked("file.txt");

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getUntracked().add("another.txt");
        });
    }

    @Test
    void testImmutabilityOfAllWorkingFiles() {
        Map<String, String> workingFiles = new HashMap<>();
        workingFiles.put("file.txt", "hash");

        WorkingDirectoryStatus status = new WorkingDirectoryStatus(workingFiles);

        assertThrows(UnsupportedOperationException.class, () -> {
            status.getAllWorkingFiles().put("another.txt", "hash2");
        });
    }
}

