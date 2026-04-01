package net.stuff691734.archipelago.events.neoforge;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;

public class PlayerDeathEvent {
    @SubscribeEvent
    public void onEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            if (event.getSource() != Archipelago.DeathLinkDamage) {
                // no looping hopefully
                Archipelago.client.sendDeathlink(
                        Archipelago.client.getMyName(),
                        event.getSource().getDeathMessage(event.getEntityLiving()).getString()
                );
            }
        }
    }
}
