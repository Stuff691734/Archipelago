package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;

public class GetCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal(Archipelago.archipelagoPersistentState.advancementChecks.toString()), true);
        context.getSource().sendSuccess(() -> Component.literal(Archipelago.archipelagoPersistentState.ftbQuestChecks.toString()), true);
        context.getSource().sendSuccess(() -> Component.literal(Archipelago.archipelagoPersistentState.slotData.toString()), true);
        context.getSource().sendSuccess(() -> Component.literal(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
        return 0;
    }

    public static int executeSpecific(CommandContext<CommandSourceStack> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        context.getSource().sendSuccess(() -> Component.literal(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkName, false).toString()), true);
        return 0;
    }
}
