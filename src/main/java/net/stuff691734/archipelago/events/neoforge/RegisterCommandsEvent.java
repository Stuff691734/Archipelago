package net.stuff691734.archipelago.events.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;

public class RegisterCommandsEvent {
    @SubscribeEvent
    public void onEvent(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        ArchipelagoCommands.register(event.getDispatcher());
    }
}
