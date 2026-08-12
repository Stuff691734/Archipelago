package net.stuff691734.archipelago.implementations;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.stuff691734.archipelagoLib.interfaces.ContextInterface;

public class ContextImpl implements ContextInterface {
    private final ICommandSender sender;

    public ContextImpl(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String s) {
        sender.sendMessage(new TextComponentString(s));
    }

    @Override
    public void sendMessageTranslatable(String s) {
        sender.sendMessage(new TextComponentTranslation(s));
    }
}
