package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.CommandDispatcher;
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
                .executes(GenerateCommand::execute)
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