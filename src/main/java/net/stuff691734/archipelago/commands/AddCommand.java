package net.stuff691734.archipelago.commands;

import net.minecraft.command.WrongUsageException;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static void execute(String[] args) throws WrongUsageException {
        if (args.length != 3) {
            throw new WrongUsageException("Usage: /archipelago add <check>");
        }

        ReceiveItemEvent.parseItem(args[1], args[2], null);
    }
}
