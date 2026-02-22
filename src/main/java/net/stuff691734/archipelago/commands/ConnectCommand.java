package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;

import java.net.URISyntaxException;

public class ConnectCommand {
    public static void execute(ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length != 3) {
            throw new WrongUsageException("Usage: /archipelago connect <name> <address>");
        }
        Archipelago.client.setName(args[1]);
        try {
            Archipelago.client.connect(args[2]);
        } catch (URISyntaxException e) {
            sender.sendMessage(new TextComponentString("Invalid server address"));
        }
        sender.sendMessage(new TextComponentString("Connected"));
    }
}
