package net.stuff691734.archipelago.events.archipealgo;

import com.google.gson.JsonObject;
import io.github.archipelagomw.ClientStatus;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import io.github.archipelagomw.network.ConnectionResult;
import net.minecraft.network.chat.TextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;
import net.stuff691734.archipelago.Utils;

public class ConnectionEvent {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);
        if (event.getResult() != ConnectionResult.Success) {
            Utils.sendMessage(new TextComponent(String.format("Connection Refused: %s",event.getResult().name())));
            return;
        }

        slotData.asMap().forEach((key, value) -> Archipelago.archipelagoPersistentState.slotData.put(key, value.getAsString()));
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

        if (Archipelago.slotData.death_link) {
            Archipelago.LOGGER.info("DeathLink activated");
            Archipelago.client.setDeathLinkEnabled(true);
            Archipelago.client.addTag("DeathLink");
        }
        Archipelago.LOGGER.info(Archipelago.archipelagoPersistentState.slotData.toString());

        for (String check : Archipelago.archipelagoPersistentState.pendingChecks) {
            Long check_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(check);
            if (check_id != null) {
                Archipelago.client.getLocationManager().checkLocation(check_id);
                if ((check).equals(Archipelago.slotData.final_goal)) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        }
        // handled, remove so they aren't given again
        Archipelago.archipelagoPersistentState.pendingChecks.clear();
        Archipelago.archipelagoPersistentState.setDirty();
    }
}
