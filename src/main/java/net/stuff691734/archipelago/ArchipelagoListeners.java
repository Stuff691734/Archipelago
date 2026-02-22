package net.stuff691734.archipelago;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class ArchipelagoListeners {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        ChecksState checksState = ChecksState.getServerState(Archipelago.server);
        if (checksState != null) {
            slotData.entrySet().forEach((entry) -> checksState.slotData.put(entry.getKey(), entry.getValue().getAsString()));
            if (checksState.slotData.getOrDefault("death_link", "0").equals("1")) {
                Archipelago.LOGGER.info("DeathLink activated");
                Archipelago.client.setDeathLinkEnabled(true);
                Archipelago.client.addTag("DeathLink");

            }
            Archipelago.LOGGER.info(checksState.slotData.toString());
        }
    }

    @ArchipelagoEventListener
    public void onDeathLink(DeathLinkEvent event) {
        Utils.sendMessage(new TextComponentString(String.format("[DeathLink] %s died: %s",event.source, event.cause)));
        for (EntityPlayerMP player : Archipelago.server.getPlayerList().getPlayers()) {
            player.onKillCommand();
        }
    }

    @ArchipelagoEventListener
    public void onArchipelagoMessage(PrintJSONEvent event) {
        Utils.sendMessage(new TextComponentString(event.apPrint.getPlainText()));
    }

    @ArchipelagoEventListener
    public void onReceiveItems(ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(new TextComponentString(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));
            ChecksState checksState = ChecksState.getServerState(Archipelago.server);
            if (checksState != null) {
                if (Utils.isAdvancementId(event.getItemName())) {
                    checksState.checks.put(event.getItemName(), true);
                } else {
                    for (EntityPlayerMP player : Archipelago.server.getPlayerList().getPlayers()) {
                        int playerLastCheck = checksState.playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0);
                        if (event.getIndex() > playerLastCheck) {

                            checksState.playerLastCheck.put(player.getCachedUniqueIdString(), (int) event.getIndex());
                            Utils.giveItem(player, event.getItemName());
                        }
                    }
                }
            }
        }
    }
}
