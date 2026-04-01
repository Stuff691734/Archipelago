package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;

public class GetCommand {
    public static int execute(CommandContext<CommandSource> context) {
        context.getSource().sendFeedback(new StringTextComponent(Archipelago.archipelagoPersistentState.advancementChecks.toString()), true);
        context.getSource().sendFeedback(new StringTextComponent(Archipelago.archipelagoPersistentState.ftbQuestChecks.toString()), true);
        context.getSource().sendFeedback(new StringTextComponent(Archipelago.archipelagoPersistentState.slotData.toString()), true);
        context.getSource().sendFeedback(new StringTextComponent(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
        return 0;
    }

    public static int executeSpecific(CommandContext<CommandSource> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        if (checkName.startsWith("adv ")) {
            context.getSource().sendFeedback(new StringTextComponent(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkName.substring(4), false).toString()), true);
        } else {
            context.getSource().sendFeedback(new StringTextComponent(Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(checkName.substring(4), false).toString()), true);
        }
        return 0;
    }
}
