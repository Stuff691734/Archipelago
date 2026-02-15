package net.stuff691734.archipelago.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class GenerateCommand extends CommandBase {

    @Override
    public String getName() {
        return "archipelago generate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/archipelago generate";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    }
}
