package net.stuff691734.archipelago.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ArchipelagoCommands extends CommandBase {

    @Override
    public String getName() {
        return "archipelago";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/archipelago";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("Usage: /archipelago <add|connect|disconnect|generate|get|open_book|setPassword>");
        }
        switch (args[0]) {
            case "add":
                AddCommand.execute(args);
                break;
            case "connect":
                ConnectCommand.execute(sender, args);
                break;
            case "disconnect":
                DisconnectCommand.execute();
                break;
            case "generate":
                GenerateCommand.execute(server, sender, args);
                break;
            case "get":
                GetCommand.execute(sender, args);
                break;
            case "open_book":
                OpenBookCommand.execute(sender, args);
                break;
            case "setPassword":
                SetPasswordCommand.execute(sender, args);
                break;
            default:
                throw new WrongUsageException("Usage: /archipelago <add|connect|disconnect|generate|get|open_book|setPassword>");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "connect", "disconnect", "generate", "get", "open_book", "setPassword");
        }
        if ((args.length == 2 || args.length == 3) && Objects.equals(args[1], "generate")) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
