package net.stuff691734.archipelago.events.archipealgo;

import com.google.gson.JsonObject;
import io.github.archipelagomw.ClientStatus;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import io.github.archipelagomw.network.ConnectionResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.*;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ConnectionEvent {
    @ArchipelagoEventListener
    public void onConnection(ConnectionResultEvent event) {
        JsonObject slotData = event.getSlotData(JsonObject.class);
        if (event.getResult() != ConnectionResult.Success) {
            Utils.sendMessage(new TextComponentString(String.format("Connection Refused: %s",event.getResult().name())));
            return;
        }

        ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance();

        if (Archipelago.getServer() != null && state != null) {
            slotData.entrySet().forEach((entry) -> state.slotData.put(entry.getKey(), entry.getValue().getAsString()));
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
            for (EntityPlayerMP player : Archipelago.getServer().getPlayerList().getPlayers()) {
                ArchipelagoPacketHandler.INSTANCE.sendTo(
                        new SyncSlotDataPacket(state.slotData),
                        player
                );
            }

            if (Archipelago.slotData.death_link) {
                Archipelago.LOGGER.info("DeathLink activated");
                Archipelago.client.setDeathLinkEnabled(true);
                Archipelago.client.addTag("DeathLink");
            }
            Archipelago.LOGGER.info(state.slotData.toString());

            for (String check : state.pendingChecks) {
                Long check_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(check);
                if (check_id != null) {
                    Archipelago.client.getLocationManager().checkLocation(check_id);
                    if ((check).equals(Archipelago.slotData.final_goal)) {
                        Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                    }
                }
            }
            // handled, remove so they aren't given again
            state.pendingChecks.clear();
            state.setDirty(true);
        }
    }
}
