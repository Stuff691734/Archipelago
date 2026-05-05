package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;

public class GetCommand {
    public static int execute(CommandContext<CommandSource> context) {
        if (ArchipelagoPersistentState.getInstance() != null) {
            context.getSource().sendFeedback(new StringTextComponent(ArchipelagoPersistentState.getInstance().advancementChecks.toString()), true);
            context.getSource().sendFeedback(new StringTextComponent(ArchipelagoPersistentState.getInstance().ftbQuestChecks.toString()), true);
            context.getSource().sendFeedback(new StringTextComponent(ArchipelagoPersistentState.getInstance().slotData.toString()), true);
            context.getSource().sendFeedback(new StringTextComponent(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
            return 0;
        }
        return 1;
    }

    public static int executeSpecific(CommandContext<CommandSource> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        if (ArchipelagoPersistentState.getInstance() != null) {
            if (checkName.startsWith("adv ")) {
                context.getSource().sendFeedback(new StringTextComponent(ArchipelagoPersistentState.getInstance().advancementChecks.getOrDefault(checkName.substring(4), false).toString()), true);
            } else {
                context.getSource().sendFeedback(new StringTextComponent(ArchipelagoPersistentState.getInstance().ftbQuestChecks.getOrDefault(checkName.substring(4), false).toString()), true);
            }
        }
        return 0;
    }
}
