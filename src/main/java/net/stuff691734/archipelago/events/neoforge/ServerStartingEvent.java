package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.flags.ItemsHandling;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoClient;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.SlotData;
import net.stuff691734.archipelago.events.archipealgo.ArchipelagoEvents;

public class ServerStartingEvent {
    @SubscribeEvent
    public void onEvent(FMLServerStartingEvent event) {
        Archipelago.server = event.getServer();
        ArchipelagoClient client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);

        ArchipelagoEvents.register(client.getEventManager());

        Archipelago.client = client;

        Archipelago.archipelagoPersistentState = ArchipelagoPersistentState.getServerState(event.getServer());

        if (!Archipelago.archipelagoPersistentState.slotData.isEmpty()) {
            Archipelago.slotData = new SlotData(
                    Archipelago.archipelagoPersistentState.slotData.get("unlock_type"),
                    Archipelago.archipelagoPersistentState.slotData.get("final_goal"),
                    Archipelago.archipelagoPersistentState.slotData.get("activated_modules"),
                    Archipelago.archipelagoPersistentState.slotData.get("advancement_check_difficulty"),
                    Archipelago.archipelagoPersistentState.slotData.get("ftb_quest_check_shape"),
                    Archipelago.archipelagoPersistentState.slotData.get("advancement_checks_give_items"),
                    Archipelago.archipelagoPersistentState.slotData.get("quest_checks_give_rewards"),
                    Archipelago.archipelagoPersistentState.slotData.get("death_link")
            );
        }
    }
}
