package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static com.example.gitter.constants.Messages.ERROR_FILE_PATH_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;

class DiffOptionsTest {

    @Test
    void testBuilderWithFilePath() {
        DiffOptions options = DiffOptions.builder()
                .file("file.txt")
                .build();

        assertEquals("file.txt", options.getFile());
    }

    @Test
    void testBuilderWithPathWithSlashes() {
        DiffOptions options = DiffOptions.builder()
                .file("src/main/java/File.java")
                .build();

        assertEquals("src/main/java/File.java", options.getFile());
    }

    @Test
    void testBuildWithNullFilePathThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DiffOptions.builder()
                    .file(null)
                    .build();
        });

        assertEquals(ERROR_FILE_PATH_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithEmptyFilePathThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DiffOptions.builder()
                    .file("")
                    .build();
        });

        assertEquals(ERROR_FILE_PATH_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithoutFilePathThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DiffOptions.builder().build();
        });

        assertEquals(ERROR_FILE_PATH_REQUIRED, exception.getMessage());
    }

    @Test
    void testGetStrategyReturnsDiffStrategy() {
        DiffOptions options = DiffOptions.builder()
                .file("file.txt")
                .build();

        CommandStrategy<DiffOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(DiffStrategy.class, strategy);
    }
}

