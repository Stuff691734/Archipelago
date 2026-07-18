package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.commands.FTBOpenBookCommand;
import net.stuff691734.archipelago.implementations.ContextImpl;
import net.stuff691734.archipelago.implementations.ServerImpl;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class ArchipelagoCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("archipelago")
            .then(literal("connect")
                .then(argument("Name", StringArgumentType.word())
                    .then(argument("WSAddress", StringArgumentType.greedyString())
                        .executes(ArchipelagoCommands::connectCommand)
                    )
                )
            )
            .then(literal("disconnect")
                .executes(ArchipelagoCommands::disconnectCommand)
            )
            .then(literal("generate")
                .executes((context) -> ArchipelagoCommands.generateCommand(context, false, true))
                .then(argument("SingleLine", BoolArgumentType.bool())
                    .executes((context) -> ArchipelagoCommands.generateCommand(context, BoolArgumentType.getBool(context, "SingleLine"), true))
                    .then(argument("RemovePermaHidden", BoolArgumentType.bool())
                        .executes((context) -> ArchipelagoCommands.generateCommand(context, BoolArgumentType.getBool(context, "SingleLine"), BoolArgumentType.getBool(context, "RemovePermaHidden")))
                    )
                )
            )
            .then(literal("get")
                .executes(ArchipelagoCommands::getCommand)
                .then(argument("check", StringArgumentType.greedyString())
                    .executes(ArchipelagoCommands::getSpecificCommand)
                )
            )
            .then(literal("add")
                .then(argument("check", StringArgumentType.greedyString())
                    .executes(ArchipelagoCommands::addCommand)
                )
            )
            .then(literal("open_book")
                .executes((context) -> ArchipelagoCommands.openBookCommand(context, null))
                .then(argument("quest_id", StringArgumentType.string())
                    .executes((context -> ArchipelagoCommands.openBookCommand(context, StringArgumentType.getString(context, "quest_id"))))
                )
            )
            .then(literal("setPassword")
                .executes((context) -> ArchipelagoCommands.passwordCommand(context, ""))
                .then(argument("password", StringArgumentType.greedyString())
                    .executes((context) -> ArchipelagoCommands.passwordCommand(context, StringArgumentType.getString(context, "password")))
                )
            )
        );
    }

    public static int disconnectCommand(CommandContext<CommandSource> context) {
        Archipelago.client.disconnect();
        return 0;
    }

    public static int addCommand(CommandContext<CommandSource> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        String[] itemName = checkName.split(" ",2);
        Archipelago.client.parseItem(itemName[0], itemName[1], null);
        return 0;
    }

    public static int generateCommand(CommandContext<CommandSource> context, boolean singleLine, boolean removePermaHidden) {
        return Archipelago.logic.generateChecks(
                new ServerImpl(context.getSource().getServer()),
                new ContextImpl(context),
                singleLine,
                removePermaHidden
        );
    }

    public static int connectCommand(CommandContext<CommandSource> context) {
        final String name = StringArgumentType.getString(context, "Name");
        final String address = StringArgumentType.getString(context, "WSAddress");
        return Archipelago.client.connectCommand(new ContextImpl(context), name, address);
    }

    public static int passwordCommand(CommandContext<CommandSource> context, String password) {
        return Archipelago.client.setPasswordCommand(new ContextImpl(context), password);
    }

    public static int openBookCommand(CommandContext<CommandSource> context, String quest_id) throws CommandSyntaxException {
        if (ModList.get().isLoaded("ftbquests")) {
            return FTBOpenBookCommand.execute(context, quest_id);
        } else {
            new ContextImpl(context).sendMessage("FTBQuests not installed");
            return 1;
        }
    }

    public static int getCommand(CommandContext<CommandSource> context) {
        return Archipelago.client.getCommand(new ContextImpl(context));
    }

    public static int getSpecificCommand(CommandContext<CommandSource> context) {
        return Archipelago.client.getSpecificCommand(new ContextImpl(context), StringArgumentType.getString(context, "check"));
    }
}