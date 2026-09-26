package net.stuff691734.archipelago.events.mod;

import net.neoforged.bus.api.IEventBus;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;

public class ModEvents {
    public static void register(IEventBus eventBus) {
        eventBus.register(new ArchipelagoPacketHandler());
    }
}
