package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.flags.ItemsHandling;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.stuff691734.archipelago.*;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;
import net.stuff691734.archipelago.events.archipealgo.ArchipelagoEvents;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ServerStartingEvent {
    @Mod.EventHandler
    public static void onEvent(FMLServerStartingEvent event) {
        Archipelago.setServer(event.getServer());
        ArchipelagoClient client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);

        ArchipelagoEvents.register(client.getEventManager());

        Archipelago.client = client;

        ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance();

        if (state != null && !state.slotData.isEmpty()) {
            Archipelago.slotData = new SlotData(
                    state.slotData.get("unlock_type"),
                    state.slotData.get("final_goal"),
                    state.slotData.get("activated_modules"),
                    state.slotData.get("advancement_check_difficulty"),
                    state.slotData.get("ftb_quest_check_shape"),
                    state.slotData.get("advancement_checks_give_items"),
                    state.slotData.get("quest_checks_give_rewards"),
                    state.slotData.get("death_link")
            );
        }

        event.registerServerCommand(new ArchipelagoCommands());
    }
}
