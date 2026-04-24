package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.stuff691734.archipelago.ftbquests.commands.FTBOpenBookCommand;

public class OpenBookCommand {
    public static void execute(ICommandSender sender, String[] args) {
        if (Loader.isModLoaded("ftbquests")) {
            FTBOpenBookCommand.execute(sender, args.length != 1 ? args[1] : "0");
        } else {
            sender.sendMessage(new TextComponentString("FTBQuests not installed"));
        }
    }
}
