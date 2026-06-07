package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;

public class SetPasswordCommand {
    public static int execute(CommandContext<CommandSource> context, String password) {
        Archipelago.client.setPassword(password);
        context.getSource().sendFeedback(new StringTextComponent("Password set successfully"), true);
        return 1;
    }
}
