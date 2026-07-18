package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelagoLib.SlotData;

public class ServerStoppingEvent {
    @SubscribeEvent
    public void onEvent(net.minecraftforge.event.server.ServerStoppingEvent event) {
        Archipelago.client.close();
        Archipelago.slotData = new SlotData();
        Archipelago.CLIENT_STATE.clear();
        Archipelago.setServer(null);
        ArchipelagoPersistentState.clearInstance();
    }
}
