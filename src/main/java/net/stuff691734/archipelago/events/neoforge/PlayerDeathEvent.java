package net.stuff691734.archipelago.events.neoforge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;

public class PlayerDeathEvent {
    @SubscribeEvent
    public void onEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            if (event.getSource() != Archipelago.DeathLinkDamage) {
                // no looping hopefully
                Archipelago.client.sendDeathlink(
                        Archipelago.client.getMyName(),
                        event.getSource().getDeathMessage(event.getEntityLiving()).getFormattedText()
                );
            }
        }
    }
}
