package net.stuff691734.archipelago.events.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.stuff691734.archipelago.Archipelago;

public class EntityExitEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Archipelago.clientState.clear();
    }
}
