package net.stuff691734.archipelago.events.mod;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;

public class PreInitEvent {
    public static void onEvent(FMLPreInitializationEvent event) {
        ArchipelagoPacketHandler.init();
    }
}
