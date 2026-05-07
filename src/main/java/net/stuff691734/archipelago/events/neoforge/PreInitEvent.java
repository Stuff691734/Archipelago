package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;

public class PreInitEvent {
    @Mod.EventHandler
    public static void onEvent(FMLPreInitializationEvent event) {
        ArchipelagoPacketHandler.init();
    }
}
