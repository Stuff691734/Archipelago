package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.stuff691734.archipelago.Archipelago;

public class GetCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TextComponent(Archipelago.archipelagoPersistentState.advancementChecks.toString()), true);
        context.getSource().sendSuccess(new TextComponent(Archipelago.archipelagoPersistentState.ftbQuestChecks.toString()), true);
        context.getSource().sendSuccess(new TextComponent(Archipelago.archipelagoPersistentState.slotData.toString()), true);
        context.getSource().sendSuccess(new TextComponent(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
        return 0;
    }

    public static int executeSpecific(CommandContext<CommandSourceStack> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        if (checkName.startsWith("adv ")) {
            context.getSource().sendSuccess(new TextComponent(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkName.substring(4), false).toString()), true);
        } else {
            context.getSource().sendSuccess(new TextComponent(Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(checkName.substring(4), false).toString()), true);
        }
        return 0;
    }
}
