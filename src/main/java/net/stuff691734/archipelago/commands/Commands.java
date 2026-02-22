package net.stuff691734.archipelago.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class Commands extends CommandBase {

    @Override
    public String getName() {
        return "archipelago";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/archipelago";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length < 1) {
            throw new WrongUsageException("Usage: /archipelago <add|connect|disconnect|generate|get>");
        }
        switch (args[0]) {
            case "add":
                AddCommand.execute(server, args);
                break;
            case "connect":
                ConnectCommand.execute(sender, args);
                break;
            case "disconnect":
                DisconnectCommand.execute();
                break;
            case "generate":
                GenerateCommand.execute(server, sender);
                break;
            case "get":
                GetCommand.execute(server, sender, args);
                break;
            default:
                throw new WrongUsageException("Usage: /archipelago <add|connect|disconnect|generate|get>");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "connect", "disconnect", "generate", "get");
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
