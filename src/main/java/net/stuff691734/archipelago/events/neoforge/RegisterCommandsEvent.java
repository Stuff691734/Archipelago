package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;

public class RegisterCommandsEvent {
    @SubscribeEvent
    public void onEvent(net.minecraftforge.event.RegisterCommandsEvent event) {
        ArchipelagoCommands.register(event.getDispatcher());
    }
}
