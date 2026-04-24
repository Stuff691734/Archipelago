package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;

public class DeathLinkEvent {
    @ArchipelagoEventListener
    public void onDeathLink(io.github.archipelagomw.events.DeathLinkEvent event) {
        Utils.sendMessage(new TextComponent(String.format("[DeathLink] %s died: %s",event.source, event.cause)));
        Archipelago.server.execute(() -> {
            for (ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
                player.hurt(Archipelago.DeathLinkDamage, Float.MAX_VALUE);
            }
        });
    }
}
