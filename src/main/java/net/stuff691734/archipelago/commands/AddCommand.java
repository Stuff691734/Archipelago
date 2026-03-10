package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.commands.CommandSourceStack;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        final String checkName = StringArgumentType.getString(context, "check");
        Archipelago.archipelagoPersistentState.advancementChecks.put(checkName, true);
        Archipelago.archipelagoPersistentState.setDirty();
        return 0;
    }
}
