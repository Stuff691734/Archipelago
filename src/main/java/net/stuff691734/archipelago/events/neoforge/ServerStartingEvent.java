package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.flags.ItemsHandling;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.stuff691734.archipelago.*;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;
import net.stuff691734.archipelago.events.archipealgo.ArchipelagoEvents;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ServerStartingEvent {
    @SubscribeEvent
    public void onEvent(FMLServerStartingEvent event) {
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
                    state.slotData.get("death_link"),
                    state.slotData.get("roots_unlocked")
            );
        }

        ArchipelagoCommands.register(event.getCommandDispatcher());
    }
}
