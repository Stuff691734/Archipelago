package net.stuff691734.archipelago;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class ArchipelagoListeners {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        ChecksState checksState = ChecksState.getServerState(Archipelago.server);

        slotData.entrySet().forEach((entry) -> checksState.slotData.put(entry.getKey(), entry.getValue().getAsString()));
        if (checksState.slotData.getOrDefault("death_link", "0").equals("1")) {
            Archipelago.LOGGER.info("DeathLink activated");
            Archipelago.client.setDeathLinkEnabled(true);
            Archipelago.client.addTag("DeathLink");

        }
        Archipelago.LOGGER.info(checksState.slotData.toString());
    }

    @ArchipelagoEventListener
    public void onDeathLink(DeathLinkEvent event) {
        Utils.sendMessage(new StringTextComponent(String.format("[DeathLink] %s died: %s",event.source, event.cause)));
        for (ServerPlayerEntity player : Archipelago.server.getPlayerList().getPlayers()) {
            player.onKillCommand();
        }
    }

    @ArchipelagoEventListener
    public void onArchipelagoMessage(PrintJSONEvent event) {
        Utils.sendMessage(new StringTextComponent(event.apPrint.getPlainText()));
    }

    @ArchipelagoEventListener
    public void onReceiveItems(ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(new StringTextComponent(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));

            if (Utils.isAdvancementId(event.getItemName())) {
                ChecksState.getServerState(Archipelago.server).checks.put(event.getItemName(), true);
            }
            else {
                for (ServerPlayerEntity player : Archipelago.server.getPlayerList().getPlayers()) {
                    int playerLastCheck = ChecksState.getServerState(Archipelago.server).playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0);
                    if (event.getIndex() > playerLastCheck) {

                        ChecksState.getServerState(Archipelago.server).playerLastCheck.put(player.getCachedUniqueIdString(), (int)event.getIndex());
                        Utils.giveItem(player, event.getItemName());
                    }
                }
            }
        }
    }
}
