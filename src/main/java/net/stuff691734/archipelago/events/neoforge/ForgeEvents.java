package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.fml.common.eventhandler.EventBus;

public class ForgeEvents {
    public static void register(EventBus eventBus) {
        eventBus.register(new EntityLoadEvent());
        eventBus.register(new PlayerDeathEvent());
    }
}
