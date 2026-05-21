package net.stuff691734.archipelago.events.neoforge;


import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;

public class EntityExitEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Archipelago.clientState.clear();
    }
}
