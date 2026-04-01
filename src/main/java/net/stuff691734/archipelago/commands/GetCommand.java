package net.stuff691734.archipelago.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;

import java.util.Objects;

public class GetCommand {
    public static void execute(ICommandSender sender, String[] args) throws WrongUsageException {
        if (args.length > 3) {
            throw new WrongUsageException("Usage: /archipelago get OR /archipelago get <check>");
        }
        if (args.length == 2 || args.length == 3) {
            executeSpecific(sender, args);
        } else {
            sender.sendMessage(new TextComponentString(Archipelago.archipelagoPersistentState.advancementChecks.toString()));
            sender.sendMessage(new TextComponentString(Archipelago.archipelagoPersistentState.ftbQuestChecks.toString()));
            sender.sendMessage(new TextComponentString(Archipelago.archipelagoPersistentState.slotData.toString()));
            sender.sendMessage(new TextComponentString(Archipelago.client.getItemManager().getReceivedItemIDs().toString()));
        }
    }

    public static void executeSpecific(ICommandSender sender, String[] check) {
        if (Objects.equals(check[1], "adv")) {
            sender.sendMessage(new TextComponentString(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(check[2], false).toString()));
        } else {
            sender.sendMessage(new TextComponentString(Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(check[2], false).toString()));
        }
    }
}
