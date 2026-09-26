package net.stuff691734.archipelago.events.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.stuff691734.archipelago.Archipelago;

public class PlayerDeathEvent {
    @SubscribeEvent
    public void onEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            if (!event.getSource().is(Archipelago.DeathLinkDamage)) {
                // no looping hopefully
                Archipelago.client.sendDeathlink(
                        Archipelago.client.getMyName(),
                        event.getSource().getLocalizedDeathMessage(event.getEntity()).getString()
                );
            }
        }
    }
}
