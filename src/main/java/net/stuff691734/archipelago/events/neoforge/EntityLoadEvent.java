package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (Archipelago.getServer() != null && ArchipelagoPersistentState.getInstance() != null) {
            int serverLastCheck = Archipelago.client.getItemManager().getIndex();
            int playerLastCheck = ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(event.getPlayer().getStringUUID(), 0);
            if (serverLastCheck > playerLastCheck) {
                ArchipelagoPersistentState.getInstance().playerLastCheck.put(event.getPlayer().getStringUUID(), serverLastCheck);

                for (NetworkItem item : Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    String[] itemName = item.itemName.split(" ", 3);
                    ReceiveItemEvent.serverParseItem(Archipelago.getServer(), ArchipelagoPersistentState.getInstance(), itemName[0], itemName[1], null);
                }
                ArchipelagoPersistentState.getInstance().setDirty();
            }
            ArchipelagoPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                new StartSyncChecksPacket(ArchipelagoPersistentState.getInstance().checks.keySet().toArray(new String[0]))
            );
            if (!ArchipelagoPersistentState.getInstance().slotData.isEmpty()) {
                ArchipelagoPacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                    new SyncSlotDataPacket(ArchipelagoPersistentState.getInstance().slotData)
                );
            }
        }
    }
}
