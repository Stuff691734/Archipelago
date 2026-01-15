package net.stuff691734.archipelago;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ArchipelagoListeners {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        ChecksState checksState = ChecksState.getServerState(Archipelago.server);

        slotData.asMap().forEach((key, value) -> checksState.slotData.put(key, value.getAsString()));
        Archipelago.LOGGER.info(checksState.slotData.toString());
    }

    @ArchipelagoEventListener
    public void onDeathLink(DeathLinkEvent event) {
        for (ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
            player.kill(player.getEntityWorld());
        }
    }

    @ArchipelagoEventListener
    public void onArchipelagoMessage(PrintJSONEvent event) {
        Utils.sendMessage(Text.literal(event.apPrint.getPlainText()));
    }

    @ArchipelagoEventListener
    public void onReceiveItems(ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(Text.literal(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));

            if (Utils.isRootAdvancementId(event.getItemName())) {
                ChecksState.getServerState(Archipelago.server).checks.put(event.getItemName(), true);
            }
            else {
                for (ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
                    Utils.giveItem(player, event.getItemName());
                }
            }
        }
    }
}
