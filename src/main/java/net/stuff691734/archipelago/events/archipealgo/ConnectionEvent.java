package net.stuff691734.archipelago.events.archipealgo;

import com.google.gson.JsonObject;
import io.github.archipelagomw.ClientStatus;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import net.stuff691734.archipelago.Archipelago;

public class ConnectionEvent {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);

        slotData.asMap().forEach((key, value) -> Archipelago.archipelagoPersistentState.slotData.put(key, value.getAsString()));
        if (Archipelago.archipelagoPersistentState.slotData.getOrDefault("death_link", "0").equals("1")) {
            Archipelago.LOGGER.info("DeathLink activated");
            Archipelago.client.setDeathLinkEnabled(true);
            Archipelago.client.addTag("DeathLink");
        }
        Archipelago.LOGGER.info(Archipelago.archipelagoPersistentState.slotData.toString());

        for (String check : Archipelago.archipelagoPersistentState.pendingChecks) {
            Long check_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(check);
            if (check_id != null) {
                Archipelago.client.getLocationManager().checkLocation(check_id);
                if ((check).equals(Archipelago.archipelagoPersistentState.slotData.get("final_goal"))) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        }
        // handled, remove so they aren't given again
        Archipelago.archipelagoPersistentState.pendingChecks.clear();
        Archipelago.archipelagoPersistentState.setDirty();
    }
}
