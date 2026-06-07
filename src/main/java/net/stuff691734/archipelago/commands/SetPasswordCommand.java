package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;

public class SetPasswordCommand {
    public static int execute(CommandContext<CommandSourceStack> context, String password) {
        Archipelago.client.setPassword(password);
        context.getSource().sendSuccess(() -> Component.literal("Password set successfully"), true);
        return 1;
    }
}
