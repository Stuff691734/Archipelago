package net.stuff691734.archipelago;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ArchipelagoListeners {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        ChecksState checksState = ChecksState.getServerState(Archipelago.server);

        slotData.asMap().forEach((key, value) -> checksState.slotData.put(key, value.getAsString()));
        if (checksState.slotData.getOrDefault("death_link", "0").equals("1")) {
            Archipelago.LOGGER.info("DeathLink activated");
            Archipelago.client.setDeathLinkEnabled(true);
            Archipelago.client.addTag("DeathLink");

        }
        Archipelago.LOGGER.info(checksState.slotData.toString());
    }

    @ArchipelagoEventListener
    public void onDeathLink(DeathLinkEvent event) {
        Utils.sendMessage(Component.literal(String.format("[DeathLink] %s died: %s",event.source, event.cause)));
        for (ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.kill();
        }
    }

    @ArchipelagoEventListener
    public void onArchipelagoMessage(PrintJSONEvent event) {
        Utils.sendMessage(Component.literal(event.apPrint.getPlainText()));
    }

    @ArchipelagoEventListener
    public void onReceiveItems(ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(Component.literal(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));

            if (Utils.isAdvancementId(event.getItemName())) {
                ChecksState.getServerState(Archipelago.server).checks.put(event.getItemName(), true);
            }
            else {
                for (ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
                    int playerLastCheck = ChecksState.getServerState(Archipelago.server).playerLastCheck.getOrDefault(player.getStringUUID(), 0);
                    if (event.getIndex() > playerLastCheck) {

                        ChecksState.getServerState(Archipelago.server).playerLastCheck.put(player.getStringUUID(), (int)event.getIndex());
                        Utils.giveItem(player, event.getItemName());
                    }
                }
            }
        }
    }
}
