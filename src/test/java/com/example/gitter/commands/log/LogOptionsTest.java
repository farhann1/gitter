package com.example.gitter.commands.log;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogOptionsTest {

    @Test
    void testBuilderCreatesDefaultOptions() {
        LogOptions options = LogOptions.builder().build();

        assertNotNull(options);
    }

    @Test
    void testGetStrategyReturnsLogStrategy() {
        LogOptions options = LogOptions.builder().build();

        CommandStrategy<LogOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(LogStrategy.class, strategy);
    }
}

