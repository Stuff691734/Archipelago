package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

public class EntityLoadEvent {
    @SubscribeEvent
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        int serverLastCheck = Archipelago.client.getItemManager().getIndex();
        int playerLastCheck = Archipelago.archipelagoPersistentState.playerLastCheck.getOrDefault(event.getEntity().getStringUUID(), 0);
        if (serverLastCheck > playerLastCheck) {
            Archipelago.archipelagoPersistentState.playerLastCheck.put(event.getEntity().getStringUUID(), serverLastCheck);

            for (NetworkItem item: Archipelago.client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                Utils.sendMessage((ServerPlayer) event.getEntity(), Component.translatable(
                        "archipelago.recieved.check",
                        item.itemName,
                        item.playerName,
                        item.locationName
                ));

                String[] itemName = item.itemName.split(" ",2);
                switch (itemName[0]) {
                    case "adv":
                        if (Utils.isAdvancementId(itemName[1])) {
                            Archipelago.archipelagoPersistentState.advancementChecks.put(itemName[1], true);
                        } else {
                            Archipelago.LOGGER.error(
                                    "Could not verify integrity of received advancement check. check: {}|advancement: {}",
                                    item.itemName,
                                    itemName[1]
                            );
                        }
                        break;
                    case "ftb":
                        if (ModList.get().isLoaded("ftbquests")) {
                            if (FTBUtils.isQuestId(itemName[1])) {
                                Archipelago.archipelagoPersistentState.ftbQuestChecks.put(itemName[1], true);
                            } else {
                                Archipelago.LOGGER.error(
                                        "Could not verify integrity of received quest check. check: {}|quest: {}",
                                        item.itemName,
                                        itemName[1]
                                );
                            }
                        }
                        break;
                    case "item":
                        if (Utils.isItemId(itemName[1])) {
                            Utils.giveItem((ServerPlayer) event.getEntity(), itemName[1]);
                        } else {
                            Archipelago.LOGGER.error(
                                    "Could not verify integrity of received item check. check: {}|item: {}",
                                    item.itemName,
                                    itemName[1]
                            );
                        }
                        break;
                    default:
                        Archipelago.LOGGER.error(
                                "Check of unknown type received. check: {}|type: {}",
                                item.itemName,
                                itemName[0]
                        );
                        break;
                }
                if (Utils.isAdvancementId(item.itemName)) {
                    Archipelago.archipelagoPersistentState.advancementChecks.put(item.itemName, true);
                }
                else {
                    Utils.giveItem((ServerPlayer) event.getEntity(), item.itemName);
                }
            }
            Archipelago.archipelagoPersistentState.setDirty();
        }
    }
}
