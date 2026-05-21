package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;

public class GetCommand {
    public static int execute(CommandContext<CommandSource> context) {
        if (ArchipelagoPersistentState.getInstance() != null) {
            context.getSource().sendSuccess(new TextComponent(ArchipelagoPersistentState.getInstance().checks.toString()), true);
            context.getSource().sendSuccess(new TextComponent(ArchipelagoPersistentState.getInstance().slotData.toString()), true);
            context.getSource().sendSuccess(new TextComponent(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), true);
            return 0;
        }
        return 1;
    }

    public static int executeSpecific(CommandContext<CommandSourceStack> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        String[] check = checkName.split(" ", 3);
        if (ArchipelagoPersistentState.getInstance() != null) {
            context.getSource().sendSuccess(new TextComponent(String.valueOf(ArchipelagoPersistentState.getCheck(check[0] + " " + check[1]))), true);
        }
        return 0;
    }
}
