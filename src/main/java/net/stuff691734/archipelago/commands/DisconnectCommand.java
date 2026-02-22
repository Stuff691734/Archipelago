package net.stuff691734.archipelago.commands;

import net.stuff691734.archipelago.Archipelago;

public class DisconnectCommand {
    public static void execute() {
        Archipelago.client.disconnect();
    }
}
