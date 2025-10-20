package com.example.gitter;

import com.example.gitter.commands.add.AddCommand;
import com.example.gitter.commands.checkout.CheckoutCommand;
import com.example.gitter.commands.commit.CommitCommand;
import com.example.gitter.commands.diff.DiffCommand;
import com.example.gitter.commands.init.InitCommand;
import com.example.gitter.commands.log.LogCommand;
import com.example.gitter.commands.reset.ResetCommand;
import com.example.gitter.commands.status.StatusCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "gitter",
    synopsisHeading = "",
    customSynopsis = {" "},
    commandListHeading = "These are common Gitter commands:%n%n",
    version = "Gitter version 1.0.0",
    subcommands = {
        InitCommand.class,
        AddCommand.class,
        CommitCommand.class,
        StatusCommand.class,
        LogCommand.class,
        DiffCommand.class,
        ResetCommand.class,
        CheckoutCommand.class,
        CommandLine.HelpCommand.class
    }
)
public class App implements Runnable {
    
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App())
            .setUsageHelpAutoWidth(true)
            .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}
