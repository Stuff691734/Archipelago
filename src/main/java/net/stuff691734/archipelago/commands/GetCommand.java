package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ChecksState;

public class GetCommand {
    public static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length > 2) {
            throw new WrongUsageException("Usage: /archipelago get OR /archipelago get <check>");
        }
        ChecksState checkState = ChecksState.getServerState(server);
        if (args.length == 2) {
            if (checkState != null) {
                sender.sendMessage(new TextComponentString(checkState.checks.getOrDefault(args[0], false).toString()));
            }
        } else {
            if (checkState != null) {
                sender.sendMessage(new TextComponentString(checkState.checks.toString()));
                sender.sendMessage(new TextComponentString(checkState.slotData.toString()));
                sender.sendMessage(new TextComponentString(checkState.playerLastCheck.toString()));
                sender.sendMessage(new TextComponentString(Archipelago.client.getItemManager().getReceivedItemIDs().toString()));
            }
        }
    }
}
