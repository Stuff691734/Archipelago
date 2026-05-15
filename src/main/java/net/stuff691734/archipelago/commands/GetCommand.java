package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.ArchipelagoPersistentState;

public class GetCommand {
    public static void execute(ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            executeSpecific(sender, args);
        } else if (ArchipelagoPersistentState.getInstance() != null) {
            sender.sendMessage(new TextComponentString(ArchipelagoPersistentState.getInstance().checks.toString()));
            sender.sendMessage(new TextComponentString(ArchipelagoPersistentState.getInstance().slotData.toString()));
        }
    }

    public static void executeSpecific(ICommandSender sender, String[] check) {
        sender.sendMessage(new TextComponentString(String.valueOf(ArchipelagoPersistentState.getCheck(check[1] + " " + check[2]))));
    }
}
