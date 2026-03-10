package net.stuff691734.archipelago.events.neoforge;

import io.github.archipelagomw.flags.ItemsHandling;
import net.neoforged.bus.api.SubscribeEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoClient;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.events.archipealgo.ArchipelagoEvents;

public class ServerStartingEvent {
    @SubscribeEvent
    public void onEvent(net.neoforged.neoforge.event.server.ServerStartingEvent event) {
        Archipelago.server = event.getServer();
        ArchipelagoClient client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);

        ArchipelagoEvents.register(client.getEventManager());

        Archipelago.client = client;

        Archipelago.archipelagoPersistentState = ArchipelagoPersistentState.getServerState(event.getServer());
    }
}
