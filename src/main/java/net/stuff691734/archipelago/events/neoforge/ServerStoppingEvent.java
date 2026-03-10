package net.stuff691734.archipelago.events.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;

public class ServerStoppingEvent {
    @SubscribeEvent
    public void onEvent(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        Archipelago.server = null;
        Archipelago.client.close();
        Archipelago.server = null;
        Archipelago.archipelagoPersistentState = null;
    }
}
