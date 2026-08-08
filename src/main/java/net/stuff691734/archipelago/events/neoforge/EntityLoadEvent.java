package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = Archipelago.getServer();
        if (server != null) {
            ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance(server);
            int serverLastCheck = Archipelago.client.getItemManager().getIndex();
            int playerLastCheck = state.playerLastCheck.getOrDefault(event.getPlayer().getCachedUniqueIdString(), 0);
            if (serverLastCheck > playerLastCheck) {
                state.playerLastCheck.put(event.getPlayer().getCachedUniqueIdString(), serverLastCheck);

                for (NetworkItem item : Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    String[] itemName = item.itemName.split(" ", 3);
                    Archipelago.client.parseItem(itemName[0], itemName[1], null);
                }
                state.setDirty(true);
            }
            ArchipelagoPacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                    new StartSyncChecksPacket(state.checks.keySet().toArray(new String[0]))
            );
            if (!state.slotData.isEmpty()) {
                ArchipelagoPacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                        new SyncSlotDataPacket(state.slotData)
                );
            }
        }
    }
}
