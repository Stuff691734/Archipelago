package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class ArchipelagoCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("archipelago")
            .then(literal("connect")
                .then(argument("Name", StringArgumentType.word())
                    .then(argument("WSAddress", StringArgumentType.greedyString())
                        .executes(ConnectCommand::execute)
                    )
                )
            )
            .then(literal("disconnect")
                .executes(DisconnectCommand::execute)
            )
            .then(literal("generate")
                .executes((context) -> GenerateCommand.execute(context, false, true))
                .then(argument("SingleLine", BoolArgumentType.bool())
                    .executes((context) -> GenerateCommand.execute(context, BoolArgumentType.getBool(context, "SingleLine"), true))
                    .then(argument("RemovePermaHidden", BoolArgumentType.bool())
                        .executes((context) -> GenerateCommand.execute(context, BoolArgumentType.getBool(context, "SingleLine"), BoolArgumentType.getBool(context, "RemovePermaHidden")))
                    )
                )
            )
            .then(literal("get")
                .executes(GetCommand::execute)
                .then(argument("check", StringArgumentType.greedyString())
                    .executes(GetCommand::executeSpecific)
                )
            )
            .then(literal("add")
                .then(argument("check", StringArgumentType.greedyString())
                    .executes(AddCommand::execute)
                )
            )
        );
    }
}