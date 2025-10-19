package com.example;

import com.example.gitter.commands.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "gitter",
    description = {
        "Gitter - A lightweight Git-like version control system",
        "",
        "Gitter is a distributed version control system for tracking changes in source code.",
        "It uses content-addressable storage (SHA-1) to maintain file integrity."
    },
    version = "Gitter version 1.0.0",
    subcommands = {
        InitCommand.class,
        CheckoutCommand.class,
        AddCommand.class,
        StatusCommand.class,
        CommitCommand.class,
        LogCommand.class,
        ResetCommand.class,
        DiffCommand.class,
        CommandLine.HelpCommand.class
    }
)
public class Main implements Runnable {
    
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main())
            .setUsageHelpAutoWidth(true)
            .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // Show help when no command is specified
        spec.commandLine().usage(System.out);
    }
}

