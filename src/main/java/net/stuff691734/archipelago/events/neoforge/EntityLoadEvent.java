package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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
            int playerLastCheck = ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(event.player.getCachedUniqueIdString(), 0);
            if (serverLastCheck > playerLastCheck) {
                ArchipelagoPersistentState.getInstance().playerLastCheck.put(event.player.getCachedUniqueIdString(), serverLastCheck);

                for (NetworkItem item : Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    String[] itemName = item.itemName.split(" ", 2);
                    ReceiveItemEvent.serverParseItem(Archipelago.getServer(), ArchipelagoPersistentState.getInstance(), itemName[0], itemName[1], null);
                }
                ArchipelagoPersistentState.getInstance().setDirty(true);
            }
            ArchipelagoPacketHandler.INSTANCE.sendTo(
                new StartSyncChecksPacket(
                    ArchipelagoPersistentState.getInstance().advancementChecks.keySet().toArray(new String[0]),
                    ArchipelagoPersistentState.getInstance().ftbQuestChecks.keySet().toArray(new String[0])
                ),
                (EntityPlayerMP) event.player
            );
            ArchipelagoPacketHandler.INSTANCE.sendTo(
                    new SyncSlotDataPacket(ArchipelagoPersistentState.getInstance().slotData),
                    (EntityPlayerMP) event.player
            );
        }
    }
}
