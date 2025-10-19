package com.example.gitter.commands.commit;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static com.example.gitter.constants.Messages.ERROR_COMMIT_MESSAGE_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;

class CommitOptionsTest {

    @Test
    void testBuilderWithSingleMessage() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test commit"})
                .build();

        assertEquals("Test commit", options.getMessage());
        assertFalse(options.isStageAll());
    }

    @Test
    void testBuilderWithMultipleMessages() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"First paragraph", "Second paragraph", "Third paragraph"})
                .build();

        assertEquals("First paragraph\n\nSecond paragraph\n\nThird paragraph", options.getMessage());
    }

    @Test
    void testBuilderWithStageAllFlag() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test"})
                .stageAll(true)
                .build();

        assertTrue(options.isStageAll());
    }

    @Test
    void testBuilderWithoutStageAllFlag() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test"})
                .stageAll(false)
                .build();

        assertFalse(options.isStageAll());
    }

    @Test
    void testBuilderDefaultStageAll() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test"})
                .build();

        assertFalse(options.isStageAll());
    }

    @Test
    void testBuildWithNullMessagesThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CommitOptions.builder()
                    .messages(null)
                    .build();
        });

        assertEquals(ERROR_COMMIT_MESSAGE_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithEmptyMessagesThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CommitOptions.builder()
                    .messages(new String[]{})
                    .build();
        });

        assertEquals(ERROR_COMMIT_MESSAGE_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithoutMessagesThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CommitOptions.builder().build();
        });

        assertEquals(ERROR_COMMIT_MESSAGE_REQUIRED, exception.getMessage());
    }

    @Test
    void testGetStrategyReturnsStandardCommitStrategy() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test"})
                .stageAll(false)
                .build();

        CommandStrategy<CommitOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(StandardCommitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyReturnsStageAllCommitStrategy() {
        CommitOptions options = CommitOptions.builder()
                .messages(new String[]{"Test"})
                .stageAll(true)
                .build();

        CommandStrategy<CommitOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(StageAllCommitStrategy.class, strategy);
    }
}

