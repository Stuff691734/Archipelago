package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;

public class DeathLinkEvent {
    @ArchipelagoEventListener
    public void onDeathLink(io.github.archipelagomw.events.DeathLinkEvent event) {
        Utils.sendMessage(new TextComponentString(String.format("[DeathLink] %s died: %s",event.source, event.cause)));
        for (EntityPlayerMP player : Archipelago.server.getPlayerList().getPlayers()) {
            player.attackEntityFrom(Archipelago.DeathLinkDamage, Float.MAX_VALUE);
        }
    }
}
