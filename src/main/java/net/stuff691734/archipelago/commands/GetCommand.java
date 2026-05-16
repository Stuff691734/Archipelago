package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;

public class GetCommand {
    public static int execute(CommandContext<CommandSource> context) {
        if (ArchipelagoPersistentState.getInstance() != null) {
            context.getSource().sendFeedback(new TextComponentString(ArchipelagoPersistentState.getInstance().checks.toString()), true);
            context.getSource().sendFeedback(new TextComponentString(ArchipelagoPersistentState.getInstance().slotData.toString()), true);
            context.getSource().sendFeedback(new TextComponentString(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
            return 0;
        }
        return 1;
    }

    public static int executeSpecific(CommandContext<CommandSource> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        String[] check = checkName.split(" ", 3);
        if (ArchipelagoPersistentState.getInstance() != null) {
            context.getSource().sendFeedback(new TextComponentString(String.valueOf(ArchipelagoPersistentState.getCheck(check[1] + " " + check[2]))), true);
        }
        return 0;
    }
}
