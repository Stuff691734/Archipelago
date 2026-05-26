package net.stuff691734.archipelago.events.mod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;

public class CommonSetupEvent {
    @SubscribeEvent
    public void onEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(ArchipelagoPacketHandler::init);
    }
}
