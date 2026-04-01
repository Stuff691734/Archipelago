package net.stuff691734.archipelago.commands;

import net.minecraft.command.WrongUsageException;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static void execute(String[] args) throws WrongUsageException {
        if (args.length != 2) {
            throw new WrongUsageException("Usage: /archipelago add <check>");
        }

        String[] itemName = args[1].split(" ",2);
        ReceiveItemEvent.parseItem(itemName[0], itemName[1], null);
    }
}
