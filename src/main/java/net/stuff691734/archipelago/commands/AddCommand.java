package net.stuff691734.archipelago.commands;

import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

import java.util.Arrays;

public class AddCommand {
    public static void execute(String[] args) {
        ReceiveItemEvent.parseItem(args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), null);
    }
}
