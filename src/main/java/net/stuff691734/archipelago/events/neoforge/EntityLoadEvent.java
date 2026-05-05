package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (Archipelago.getServer() != null && ArchipelagoPersistentState.getInstance() != null) {
            int serverLastCheck = Archipelago.client.getItemManager().getIndex();
            int playerLastCheck = ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(event.getPlayer().getCachedUniqueIdString(), 0);
            if (serverLastCheck > playerLastCheck) {
                ArchipelagoPersistentState.getInstance().playerLastCheck.put(event.getPlayer().getCachedUniqueIdString(), serverLastCheck);

                for (NetworkItem item : Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    String[] itemName = item.itemName.split(" ", 2);
                    ReceiveItemEvent.serverParseItem(Archipelago.getServer(), ArchipelagoPersistentState.getInstance(), itemName[0], itemName[1], null);
                }
                ArchipelagoPersistentState.getInstance().setDirty(true);
            }
        }
    }
}
