package net.stuff691734.archipelago.events.mod;

import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

public class ServerStoppingEvent {
    public static void onEvent(FMLServerStoppingEvent event) {
        Archipelago.server = null;
        Archipelago.client.close();
        Archipelago.server = null;
        Archipelago.archipelagoPersistentState = null;
        Archipelago.slotData = new SlotData();
    }
}
