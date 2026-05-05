package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

public class ServerStoppingEvent {
    @SubscribeEvent
    public void onEvent(FMLServerStoppingEvent event) {
        Archipelago.client.close();
        Archipelago.slotData = new SlotData();
    }
}
