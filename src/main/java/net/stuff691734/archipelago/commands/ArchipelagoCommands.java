package net.stuff691734.archipelago.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.commands.FTBOpenBookCommand;
import net.stuff691734.archipelago.implementations.ContextImpl;
import net.stuff691734.archipelago.implementations.ServerImpl;

import javax.annotation.Nullable;
import java.util.Arrays;
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
                this.addCommand(args);
                break;
            case "connect":
                this.connectCommand(sender, args);
                break;
            case "disconnect":
                this.disconnectCommand();
                break;
            case "generate":
                this.generateCommand(server, sender, args);
                break;
            case "get":
                this.getCommand(sender, args);
                break;
            case "open_book":
                this.openBookCommand(sender, args);
                break;
            case "setPassword":
                this.passwordCommand(sender, args);
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

    public void disconnectCommand() {
        Archipelago.client.disconnect();
    }

    public void addCommand(String[] args) {
        Archipelago.client.parseItem(args[1], CommandBase.buildString(args, 2), null);
    }

    public void generateCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean singleLine = false;
        if (args.length >= 2) {
            singleLine = CommandBase.parseBoolean(args[1]);
        }

        boolean removePermaHidden = true;
        if (args.length >= 3) {
            removePermaHidden = !CommandBase.parseBoolean(args[2]);
        }

        Archipelago.client.generateCommand(
                Archipelago.logic,
                new ContextImpl(sender),
                singleLine,
                removePermaHidden
        );
    }

    public void connectCommand(ICommandSender sender, String[] args) {
        String fullString = CommandBase.buildString(args, 1);
        if (args[1].startsWith("\"") || args[1].startsWith("'")) {
            char quoteChar = args[1].charAt(0);
            String[] strings = fullString.split(String.valueOf(quoteChar), 3);
            Archipelago.client.connectCommand(new ContextImpl(sender), strings[1], strings[2].startsWith(" ") ? strings[2].substring(1) : strings[2]);
        } else {
            Archipelago.client.connectCommand(new ContextImpl(sender), args[1], CommandBase.buildString(args, 2));
        }
    }

    public void passwordCommand(ICommandSender sender, String[] args) {
        Archipelago.client.setPasswordCommand(new ContextImpl(sender), CommandBase.buildString(args, 1));
    }

    public void openBookCommand(ICommandSender sender, String[] args) {
        if (Loader.isModLoaded("ftbquests")) {
            FTBOpenBookCommand.execute(sender, args.length != 1 ? args[1] : "0");
        } else {
            new ContextImpl(sender).sendMessage("FTBQuests not installed");
        }
    }

    public void getCommand(ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            Archipelago.client.getSpecificCommand(new ContextImpl(sender), CommandBase.buildString(args, 1));
        } else {
            Archipelago.client.getCommand(new ContextImpl(sender));
        }
    }
}
