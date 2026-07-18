package net.stuff691734.archipelago.implementations;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelagoLib.interfaces.ContextInterface;

public class ContextImpl implements ContextInterface {
    private final CommandContext<CommandSourceStack> context;

    public ContextImpl(CommandContext<CommandSourceStack> context) {
        this.context = context;
    }

    @Override
    public void sendMessage(String s) {
        this.context.getSource().sendSuccess(() -> Component.literal(s), false);
    }

    @Override
    public void sendMessageTranslatable(String s) {
        this.context.getSource().sendSuccess(() -> Component.translatable(s), false);
    }
}
