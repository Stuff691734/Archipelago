package net.stuff691734.archipelago.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public interface OnDeathEvent {
    Event<OnDeathEvent> EVENT = EventFactory.createArrayBacked(OnDeathEvent.class, (listeners) -> (player, damageSource) -> {
        for (OnDeathEvent listener : listeners) {
            listener.interact(player, damageSource);
        }
    });

    void interact(ServerPlayerEntity player, DamageSource damageSource);
}
