package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

import javax.annotation.Nullable;

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

            ReceiveItemEvent.parseItem(itemName[0], itemName[1], event.getIndex());
        }
    }

    public static void parseItem(String itemType, String itemName, @Nullable Long index) {
        switch (itemType) {
            case "adv":
                if (Utils.isAdvancementId(itemName)) {
                    Archipelago.archipelagoPersistentState.advancementChecks.put(itemName, true);
                } else {
                    Archipelago.LOGGER.error(
                            "Could not verify integrity of received advancement check. advancement: {}",
                            itemName
                    );
                }
                break;
            case "ftb":
                if (ModList.get().isLoaded("ftbquests")) {
                    if (FTBUtils.isQuestId(itemName)) {
                        Archipelago.LOGGER.info("Added Ftb Quest Item");
                        Archipelago.archipelagoPersistentState.ftbQuestChecks.put(itemName, true);
                    } else {
                        Archipelago.LOGGER.error(
                                "Could not verify integrity of received quest check. quest: {}",
                                itemName
                        );
                    }
                }
                break;
            case "item":
                if (Utils.isItemId(itemName)) {
                    Utils.giveItem(Archipelago.server, itemName, index);
                } else {
                    Archipelago.LOGGER.error(
                            "Could not verify integrity of received item check. item: {}",
                            itemName
                    );
                }
                break;
            default:
                Archipelago.LOGGER.error(
                        "Check of unknown type received. name: {}|type: {}",
                        itemName,
                        itemType
                );
                break;
        }
        Archipelago.archipelagoPersistentState.setDirty();
    }
}
