package com.example.gitter.commands.init;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitOptionsTest {

    @Test
    void testBuilderCreatesDefaultOptions() {
        InitOptions options = InitOptions.builder().build();

        assertNotNull(options);
    }

    @Test
    void testGetStrategyReturnsInitStrategy() {
        InitOptions options = InitOptions.builder().build();

        CommandStrategy<InitOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(InitStrategy.class, strategy);
    }
}
