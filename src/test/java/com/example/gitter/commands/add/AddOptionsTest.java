package com.example.gitter.commands.add;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.gitter.constants.Messages.ERROR_FILE_PATTERN_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;

class AddOptionsTest {

    @Test
    void testBuilderWithSingleFilePattern() {
        AddOptions options = AddOptions.builder()
                .files(List.of("file.txt"))
                .build();

        assertEquals(1, options.getFiles().size());
        assertEquals("file.txt", options.getFiles().get(0));
    }

    @Test
    void testBuilderWithMultipleFilePatterns() {
        AddOptions options = AddOptions.builder()
                .files(List.of("file1.txt", "file2.txt", "*.java"))
                .build();

        assertEquals(3, options.getFiles().size());
        assertTrue(options.getFiles().contains("file1.txt"));
        assertTrue(options.getFiles().contains("file2.txt"));
        assertTrue(options.getFiles().contains("*.java"));
    }

    @Test
    void testBuilderWithDotPattern() {
        AddOptions options = AddOptions.builder()
                .files(List.of("."))
                .build();

        assertEquals(1, options.getFiles().size());
        assertEquals(".", options.getFiles().get(0));
    }

    @Test
    void testBuildWithNullFilePatternsThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AddOptions.builder()
                    .files(null)
                    .build();
        });

        assertEquals(ERROR_FILE_PATTERN_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithEmptyFilePatternsThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AddOptions.builder()
                    .files(List.of())
                    .build();
        });

        assertEquals(ERROR_FILE_PATTERN_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithoutFilePatternsThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AddOptions.builder().build();
        });

        assertEquals(ERROR_FILE_PATTERN_REQUIRED, exception.getMessage());
    }

    @Test
    void testGetStrategyReturnsAddStrategy() {
        AddOptions options = AddOptions.builder()
                .files(List.of("file.txt"))
                .build();

        CommandStrategy<AddOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(AddStrategy.class, strategy);
    }

    @Test
    void testGetFilesReturnsImmutableList() {
        AddOptions options = AddOptions.builder()
                .files(List.of("file.txt"))
                .build();

        assertThrows(UnsupportedOperationException.class, () -> {
            options.getFiles().add("another.txt");
        });
    }
}

