package net.stuff691734.archipelago.events.mod;

import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelagoLib.SlotData;

public class ServerStoppingEvent {
    public static void onEvent(FMLServerStoppingEvent event) {
        Archipelago.client.close();
        Archipelago.slotData = new SlotData();
        Archipelago.CLIENT_STATE.clear();
        Archipelago.setServer(null);
        ArchipelagoPersistentState.clearInstance();
    }
}
