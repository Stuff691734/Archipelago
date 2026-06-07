package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;

public class SetPasswordCommand {
    public static void execute(ICommandSender sender, String[] args) {
        String password = "";
        if (args.length > 1) {
            password = String.join(" ", args);
        }
        Archipelago.client.setPassword(password);
        sender.sendMessage(new TextComponentString("Password set successfully"));
    }
}
