package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;
import net.stuff691734.archipelagoLib.Logic;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = Archipelago.getServer();
        if (server != null) {
            ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance(server);
            int serverLastCheck = Archipelago.client.getItemManager().getIndex();
            int playerLastCheck = state.playerLastCheck.getOrDefault(event.getEntity().getStringUUID(), 0);
            if (serverLastCheck > playerLastCheck) {
                state.playerLastCheck.put(event.getEntity().getStringUUID(), serverLastCheck);

                for (NetworkItem item : Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    String[] itemName = item.itemName.split(" ", 3);
                    Archipelago.client.parseItem(itemName[0], itemName[1], null);
                }
                state.setDirty();
            }
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new StartSyncChecksPacket(state.checks.keySet().stream().toList()));
            if (!state.slotData.isEmpty()) {
                PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SyncSlotDataPacket(state.slotData));
            }
        }
    }
}
