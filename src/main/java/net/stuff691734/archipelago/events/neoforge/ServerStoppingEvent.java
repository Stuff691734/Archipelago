package net.stuff691734.archipelago.events.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.SlotData;

public class ServerStoppingEvent {
    @SubscribeEvent
    public void onEvent(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        Archipelago.client.close();
        Archipelago.slotData = new SlotData();
        Archipelago.clientState.clear();
        Archipelago.setServer(null);
        ArchipelagoPersistentState.clearInstance();
    }
}
