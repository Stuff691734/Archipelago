package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;

public class DeathLinkEvent {
    @ArchipelagoEventListener
    public void onDeathLink(io.github.archipelagomw.events.DeathLinkEvent event) {
        if (Archipelago.getServer() != null) {
            Utils.sendMessage(new StringTextComponent(String.format("[DeathLink] %s died: %s", event.source, event.cause)));
            Archipelago.getServer().execute(() -> {
                for (ServerPlayerEntity player : Archipelago.getServer().getPlayerList().getPlayers()) {
                    player.hurt(Archipelago.DeathLinkDamage, Float.MAX_VALUE);
                }
            });
        }
    }
}
