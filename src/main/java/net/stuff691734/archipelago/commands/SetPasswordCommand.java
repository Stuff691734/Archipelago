package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.stuff691734.archipelago.Archipelago;

public class SetPasswordCommand {
    public static int execute(CommandContext<CommandSourceStack> context, String password) {
        Archipelago.client.setPassword(password);
        context.getSource().sendSuccess(new TextComponent("Password set successfully"), true);
        return 1;
    }
}
