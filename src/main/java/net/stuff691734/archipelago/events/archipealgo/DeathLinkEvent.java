package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;

public class DeathLinkEvent {
    @ArchipelagoEventListener
    public void onDeathLink(io.github.archipelagomw.events.DeathLinkEvent event) {
        if (Archipelago.getServer() != null) {
            Utils.sendMessage(Component.literal(String.format("[DeathLink] %s died: %s", event.source, event.cause)));
            Archipelago.getServer().execute(() -> {
                for (ServerPlayer player : Archipelago.getServer().getPlayerList().getPlayers()) {
                    player.hurt(Archipelago.DeathLinkDamage, Float.MAX_VALUE);
                }
            });
        }
    }
}
