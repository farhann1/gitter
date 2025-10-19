package com.example.gitter.commands.strategy;

import java.io.IOException;

/**
 * Common strategy interface for all command options.
 */
public interface CommandStrategy<T> {
    Integer execute(T options) throws IOException;
}
