package net.stuff691734.archipelago.commands;

import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.stuff691734.archipelago.ChecksState;

public class AddCommand {
    public static void execute(MinecraftServer server, String[] args) throws WrongUsageException {
        if (args.length != 2) {
            throw new WrongUsageException("Usage: /archipelago add <check>");
        }
        ChecksState checkState = ChecksState.getServerState(server);
        if (checkState != null) {
            checkState.checks.put(args[1], true);
        }
    }
}
