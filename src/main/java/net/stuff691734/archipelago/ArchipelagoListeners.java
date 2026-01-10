package net.stuff691734.archipelago;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
        if (Archipelago.server != null) {
            for (ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
                player.kill();
            }
        }
    }

    @ArchipelagoEventListener
    public void onArchipelagoMessage(PrintJSONEvent event) {
        if (Archipelago.server != null) {
            Archipelago.server.sendMessage(Text.literal(event.apPrint.getPlainText()));
        }
    }

    @ArchipelagoEventListener
    public void onReceiveItems(ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Archipelago.server.sendMessage(Text.literal(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));

            if (Utils.isRootAdvancementId(event.getItemName())) {
                ChecksState.getServerState(Archipelago.server).checks.put(event.getItemName(), true);
            }
            else {
                ChecksState checksState = ChecksState.getServerState(Archipelago.server);
                String[] strings = event.getItemName().split(" ");
                int amount = Integer.parseInt(strings[0]);
                ItemStack item = new ItemStack(Registries.ITEM.get(Identifier.of(strings[1])), amount);
                for (ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
                    if (!player.giveItemStack(item)) {
                        player.dropStack(item);
                    }
                    checksState.playerLastCheck.put(player.getUuidAsString(), (int)event.getIndex());
                }
            }
        }
    }
}
