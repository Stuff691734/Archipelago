package net.stuff691734.archipelago.implementations;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.stuff691734.archipelagoLib.interfaces.ContextInterface;

public class ContextImpl implements ContextInterface {
    private final CommandContext<CommandSource> context;

    public ContextImpl(CommandContext<CommandSource> context) {
        this.context = context;
    }

    @Override
    public void sendMessage(String s) {
        this.context.getSource().sendSuccess(new StringTextComponent(s), false);
    }

    @Override
    public void sendMessageTranslatable(String s) {
        this.context.getSource().sendSuccess(new TranslationTextComponent(s), false);
    }
}
