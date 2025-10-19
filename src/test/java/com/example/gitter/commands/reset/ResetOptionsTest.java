package com.example.gitter.commands.reset;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResetOptionsTest {

    @Test
    void testBuilderWithEmptyArgs() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of())
                .build();

        assertTrue(options.isEmpty());
        assertNull(options.getFirst());
        assertEquals(0, options.getArgs().size());
    }

    @Test
    void testBuilderWithNullArgs() {
        ResetOptions options = ResetOptions.builder()
                .args(null)
                .build();

        assertTrue(options.isEmpty());
        assertNull(options.getFirst());
    }

    @Test
    void testBuilderWithSingleArg() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("HEAD~1"))
                .build();

        assertFalse(options.isEmpty());
        assertEquals("HEAD~1", options.getFirst());
        assertEquals(1, options.getArgs().size());
    }

    @Test
    void testBuilderWithMultipleArgs() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("file1.txt", "file2.txt", "file3.txt"))
                .build();

        assertFalse(options.isEmpty());
        assertEquals("file1.txt", options.getFirst());
        assertEquals(3, options.getArgs().size());
    }

    @Test
    void testGetStrategyWithEmptyArgsReturnsResetToCommitStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of())
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(ResetToCommitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyWithHEADReturnsResetToCommitStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("HEAD"))
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(ResetToCommitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyWithHEADTildeReturnsResetToCommitStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("HEAD~5"))
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(ResetToCommitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyWithFilePatternReturnsUnstageFilesStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("file.txt"))
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(UnstageFilesStrategy.class, strategy);
    }

    @Test
    void testGetStrategyWithMultipleFilesReturnsUnstageFilesStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("file1.txt", "file2.txt"))
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(UnstageFilesStrategy.class, strategy);
    }

    @Test
    void testGetStrategyWithWildcardReturnsUnstageFilesStrategy() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("*.txt"))
                .build();

        CommandStrategy<ResetOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(UnstageFilesStrategy.class, strategy);
    }

    @Test
    void testGetArgsReturnsImmutableList() {
        ResetOptions options = ResetOptions.builder()
                .args(List.of("file.txt"))
                .build();

        assertThrows(UnsupportedOperationException.class, () -> {
            options.getArgs().add("another.txt");
        });
    }
}

