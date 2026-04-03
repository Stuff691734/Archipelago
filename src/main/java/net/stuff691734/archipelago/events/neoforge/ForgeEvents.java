package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeEvents {
    public static void register(IEventBus eventBus) {
        eventBus.register(new EntityLoadEvent());
        eventBus.register(new PlayerDeathEvent());
        eventBus.register(new ServerStartingEvent());
        eventBus.register(new ServerStoppingEvent());
    }
}
