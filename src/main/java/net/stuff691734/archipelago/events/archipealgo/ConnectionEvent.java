package net.stuff691734.archipelago.events.archipealgo;

import com.google.gson.JsonObject;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import net.stuff691734.archipelago.Archipelago;

public class ConnectionEvent {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        slotData.asMap().forEach((key, value) -> Archipelago.archipelagoPersistentState.slotData.put(key, value.getAsString()));
        Archipelago.archipelagoPersistentState.setDirty();
        if (Archipelago.archipelagoPersistentState.slotData.getOrDefault("death_link", "0").equals("1")) {
            Archipelago.LOGGER.info("DeathLink activated");
            Archipelago.client.setDeathLinkEnabled(true);
            Archipelago.client.addTag("DeathLink");

        }
        Archipelago.LOGGER.info(Archipelago.archipelagoPersistentState.slotData.toString());
    }
}
