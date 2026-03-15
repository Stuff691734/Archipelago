package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        String[] itemName = checkName.split(" ",2);
        ReceiveItemEvent.parseItem(itemName[0], itemName[1], null);
        Archipelago.archipelagoPersistentState.setDirty();
        return 0;
    }
}
