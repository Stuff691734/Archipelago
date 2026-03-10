package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

public class ReceiveItemEvent {
    @ArchipelagoEventListener
    public void onReceiveItems(io.github.archipelagomw.events.ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(Component.literal(String.format(
                    "Received %s from %s (%s)",
                    event.getItemName(),
                    event.getPlayerName(),
                    event.getLocationName()
            )));
            String[] itemName = event.getItemName().split(" ",2);
            switch (itemName[0]) {
                case "adv":
                    if (Utils.isAdvancementId(itemName[1])) {
                        Archipelago.archipelagoPersistentState.advancementChecks.put(itemName[1], true);
                    } else {
                        Archipelago.LOGGER.error(
                                "Could not verify integrity of received advancement check. check: {}|advancement: {}",
                                event.getItemName(),
                                itemName[1]
                        );
                    }
                    break;
                case "ftb":
                    if (ModList.get().isLoaded("ftbquests")) {
                        if (FTBUtils.isQuestId(itemName[1])) {
                            Archipelago.LOGGER.info("Added Ftb Quest Item");
                            Archipelago.archipelagoPersistentState.ftbQuestChecks.put(itemName[1], true);
                        } else {
                            Archipelago.LOGGER.error(
                                    "Could not verify integrity of received quest check. check: {}|quest: {}",
                                    event.getItemName(),
                                    itemName[1]
                            );
                        }
                    }
                    break;
                case "item":
                    if (Utils.isItemId(itemName[1])) {
                        Utils.giveItem(Archipelago.server, itemName[1]);
                    } else {
                        Archipelago.LOGGER.error(
                                "Could not verify integrity of received item check. check: {}|item: {}",
                                event.getItemName(),
                                itemName[1]
                        );
                    }
                    break;
                default:
                    Archipelago.LOGGER.error(
                            "Check of unknown type received. check: {}|type: {}",
                            event.getItemName(),
                            itemName[0]
                    );
                    break;
            }
            Archipelago.archipelagoPersistentState.setDirty();
        }
    }
}
