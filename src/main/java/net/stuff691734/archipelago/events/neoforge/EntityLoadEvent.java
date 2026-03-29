package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.events.archipealgo.ReceiveItemEvent;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        int serverLastCheck = Archipelago.client.getItemManager().getIndex();
        int playerLastCheck = Archipelago.archipelagoPersistentState.playerLastCheck.getOrDefault(event.getEntity().getStringUUID(), 0);
        if (serverLastCheck > playerLastCheck) {
            Archipelago.archipelagoPersistentState.playerLastCheck.put(event.getEntity().getStringUUID(), serverLastCheck);

            for (NetworkItem item: Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                String[] itemName = item.itemName.split(" ",2);
                ReceiveItemEvent.playerParseItem((ServerPlayerEntity) event.getEntity(), itemName[0], itemName[1], null);
            }
            Archipelago.archipelagoPersistentState.setDirty();
        }
    }
}
