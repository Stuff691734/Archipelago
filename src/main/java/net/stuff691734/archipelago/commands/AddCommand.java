package net.stuff691734.archipelago.commands;

import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class AddCommand {
    public static void execute(String[] args) {
        ReceiveItemEvent.parseItem(args[1], args[2], null);
    }
}
