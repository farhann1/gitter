package com.example.gitter.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.gitter.constants.PathConstants.GITTER;
import static org.junit.jupiter.api.Assertions.*;

class GitterIgnoreTest {

    private Path testRepoRoot;
    private Path gitterIgnoreFile;

    @BeforeEach
    void setUp() throws IOException {
        testRepoRoot = GITTER.getParent();
        gitterIgnoreFile = testRepoRoot.resolve(".gitterignore");
        
        // Reset singleton instance via reflection for each test
        try {
            var instanceField = GitterIgnore.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset GitterIgnore singleton", e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(gitterIgnoreFile)) {
            Files.delete(gitterIgnoreFile);
        }
        
        // Reset singleton instance
        try {
            var instanceField = GitterIgnore.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset GitterIgnore singleton", e);
        }
    }

    @Test
    void testNoGitterIgnoreFile() throws IOException {
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // .gitter directory should always be ignored
        assertTrue(gitterIgnore.shouldIgnore(".gitter"));
        assertTrue(gitterIgnore.shouldIgnore(".gitter/index"));
        assertTrue(gitterIgnore.shouldIgnore(".gitter/objects/abc123"));
        
        // .gitterignore file should always be ignored
        assertTrue(gitterIgnore.shouldIgnore(".gitterignore"));
        
        // Other files should not be ignored
        assertFalse(gitterIgnore.shouldIgnore("README.md"));
        assertFalse(gitterIgnore.shouldIgnore("src/App.java"));
    }

    @Test
    void testDirectoryExclusion() throws IOException {
        Files.writeString(gitterIgnoreFile, "build/\ntarget/\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // Directories themselves should be ignored
        assertTrue(gitterIgnore.shouldIgnore("build"));
        assertTrue(gitterIgnore.shouldIgnore("target"));
        
        // Files inside directories should be ignored
        assertTrue(gitterIgnore.shouldIgnore("build/output.jar"));
        assertTrue(gitterIgnore.shouldIgnore("target/classes/App.class"));
        assertTrue(gitterIgnore.shouldIgnore("build/test/file.txt"));
        
        // Similar-named directories elsewhere should NOT be ignored
        assertFalse(gitterIgnore.shouldIgnore("src/build/file.txt"));
        assertFalse(gitterIgnore.shouldIgnore("mybuild/file.txt"));
    }

    @Test
    void testExactFilePathExclusion() throws IOException {
        Files.writeString(gitterIgnoreFile, ".DS_Store\nsrc/.DS_Store\nlogs/app.log\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // Exact paths should be ignored
        assertTrue(gitterIgnore.shouldIgnore(".DS_Store"));
        assertTrue(gitterIgnore.shouldIgnore("src/.DS_Store"));
        assertTrue(gitterIgnore.shouldIgnore("logs/app.log"));
        
        // Different paths should NOT be ignored
        assertFalse(gitterIgnore.shouldIgnore("logs/.DS_Store"));
        assertFalse(gitterIgnore.shouldIgnore("app.log"));
        assertFalse(gitterIgnore.shouldIgnore("logs/debug/app.log"));
    }

    @Test
    void testMixedDirectoriesAndFiles() throws IOException {
        Files.writeString(gitterIgnoreFile, "build/\ntarget/\nsecrets.env\nsrc/Test.java\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // Directories
        assertTrue(gitterIgnore.shouldIgnore("build"));
        assertTrue(gitterIgnore.shouldIgnore("build/output.jar"));
        assertTrue(gitterIgnore.shouldIgnore("target"));
        assertTrue(gitterIgnore.shouldIgnore("target/classes/App.class"));
        
        // Exact file paths
        assertTrue(gitterIgnore.shouldIgnore("secrets.env"));
        assertTrue(gitterIgnore.shouldIgnore("src/Test.java"));
        
        // Should NOT be ignored
        assertFalse(gitterIgnore.shouldIgnore("src/secrets.env"));
        assertFalse(gitterIgnore.shouldIgnore("Test.java"));
        assertFalse(gitterIgnore.shouldIgnore("src/main/Test.java"));
    }

    @Test
    void testEmptyLinesAndWhitespace() throws IOException {
        Files.writeString(gitterIgnoreFile, "build/\n\n  \ntarget/\n   \n.DS_Store\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        assertTrue(gitterIgnore.shouldIgnore("build"));
        assertTrue(gitterIgnore.shouldIgnore("target"));
        assertTrue(gitterIgnore.shouldIgnore(".DS_Store"));
    }

    @Test
    void testTrailingSlashRemoval() throws IOException {
        Files.writeString(gitterIgnoreFile, "build/\nnode_modules/\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // Directory name without slash should match
        assertTrue(gitterIgnore.shouldIgnore("build"));
        assertTrue(gitterIgnore.shouldIgnore("node_modules"));
        
        // Contents should match
        assertTrue(gitterIgnore.shouldIgnore("build/file.txt"));
        assertTrue(gitterIgnore.shouldIgnore("node_modules/package.json"));
    }

    @Test
    void testNestedDirectories() throws IOException {
        Files.writeString(gitterIgnoreFile, "logs/\n");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        assertTrue(gitterIgnore.shouldIgnore("logs"));
        assertTrue(gitterIgnore.shouldIgnore("logs/app.log"));
        assertTrue(gitterIgnore.shouldIgnore("logs/debug/error.log"));
        assertTrue(gitterIgnore.shouldIgnore("logs/debug/test/file.txt"));
        
        assertFalse(gitterIgnore.shouldIgnore("src/logs/file.txt"));
    }

    @Test
    void testSingletonPattern() throws IOException {
        Files.writeString(gitterIgnoreFile, "build/\n");
        
        GitterIgnore instance1 = GitterIgnore.getInstance();
        GitterIgnore instance2 = GitterIgnore.getInstance();
        
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    void testAlwaysIgnoresGitterDirectory() throws IOException {
        Files.writeString(gitterIgnoreFile, "");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        assertTrue(gitterIgnore.shouldIgnore(".gitter"));
        assertTrue(gitterIgnore.shouldIgnore(".gitter/index"));
        assertTrue(gitterIgnore.shouldIgnore(".gitter/objects/abc123"));
        assertTrue(gitterIgnore.shouldIgnore(".gitter/refs/heads/main"));
    }

    @Test
    void testAlwaysIgnoresGitterIgnoreFile() throws IOException {
        Files.writeString(gitterIgnoreFile, "");
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        assertTrue(gitterIgnore.shouldIgnore(".gitterignore"));
    }

    @Test
    void testComplexScenario() throws IOException {
        String content = """
                build/
                target/
                node_modules/
                .DS_Store
                secrets.env
                src/Test.java
                logs/app.log
                """;
        Files.writeString(gitterIgnoreFile, content);
        GitterIgnore gitterIgnore = GitterIgnore.getInstance();
        
        // Directories
        assertTrue(gitterIgnore.shouldIgnore("build"));
        assertTrue(gitterIgnore.shouldIgnore("build/output.jar"));
        assertTrue(gitterIgnore.shouldIgnore("target/classes/App.class"));
        assertTrue(gitterIgnore.shouldIgnore("node_modules/package.json"));
        
        // Exact files
        assertTrue(gitterIgnore.shouldIgnore(".DS_Store"));
        assertTrue(gitterIgnore.shouldIgnore("secrets.env"));
        assertTrue(gitterIgnore.shouldIgnore("src/Test.java"));
        assertTrue(gitterIgnore.shouldIgnore("logs/app.log"));
        
        // Should NOT be ignored
        assertFalse(gitterIgnore.shouldIgnore("README.md"));
        assertFalse(gitterIgnore.shouldIgnore("src/App.java"));
        assertFalse(gitterIgnore.shouldIgnore("src/.DS_Store"));
        assertFalse(gitterIgnore.shouldIgnore("app.log"));
        assertFalse(gitterIgnore.shouldIgnore("Test.java"));
    }
}

