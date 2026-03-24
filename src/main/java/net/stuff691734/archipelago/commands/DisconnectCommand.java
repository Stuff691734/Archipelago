package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.stuff691734.archipelago.Archipelago;

public class DisconnectCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        Archipelago.client.disconnect();
        return 0;
    }
}
