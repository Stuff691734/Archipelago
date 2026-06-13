package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static int execute(CommandContext<CommandSource> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        String[] itemName = checkName.split(" ",2);
        ReceiveItemEvent.parseItem(itemName[0], itemName[1], null);
        return 0;
    }
}
