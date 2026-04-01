package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.stuff691734.archipelago.Archipelago;

public class DisconnectCommand {
    public static int execute(CommandContext<CommandSource> context) {
        Archipelago.client.disconnect();
        return 0;
    }
}
