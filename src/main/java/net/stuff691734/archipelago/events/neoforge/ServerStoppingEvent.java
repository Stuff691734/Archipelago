package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

public class ServerStoppingEvent {
    @Mod.EventHandler
    public static void onEvent(FMLServerStoppingEvent event) {
        Archipelago.server = null;
        Archipelago.client.close();
        Archipelago.server = null;
        Archipelago.archipelagoPersistentState = null;
        Archipelago.slotData = new SlotData();
    }
}
