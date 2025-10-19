package com.example.gitter.commands.status;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusOptionsTest {

    @Test
    void testBuilderCreatesDefaultOptions() {
        StatusOptions options = StatusOptions.builder().build();

        assertNotNull(options);
    }

    @Test
    void testGetStrategyReturnsStatusStrategy() {
        StatusOptions options = StatusOptions.builder().build();

        CommandStrategy<StatusOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(StatusStrategy.class, strategy);
    }
}

