package com.example.gitter.commands.diff;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiffOptionsTest {

    @Test
    void testBuilderCreatesOptions() {
        DiffOptions options = DiffOptions.builder().build();
        assertNotNull(options);
    }

    @Test
    void testGetStrategyReturnsDiffStrategy() {
        DiffOptions options = DiffOptions.builder().build();

        CommandStrategy<DiffOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(DiffStrategy.class, strategy);
    }
}

