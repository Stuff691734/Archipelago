package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;

import javax.annotation.Nullable;

public class ReceiveItemEvent {
    @ArchipelagoEventListener
    public void onReceiveItems(io.github.archipelagomw.events.ReceiveItemEvent event) {
        if (Archipelago.server != null) {
            Utils.sendMessage(new TextComponent(String.format(
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
        for (ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            playerParseItem(player, itemType, itemName, index);
        }
        switch (itemType) {
            case "adv":
                if (Utils.isAdvancementId(itemName)) {
                    Archipelago.archipelagoPersistentState.advancementChecks.put(itemName, true);
                }
                break;
            case "ftb":
                Archipelago.LOGGER.error("Got FTB check on version without FTB Quests");
                break;
        }
        Archipelago.archipelagoPersistentState.setDirty();
    }

    public static void playerParseItem(ServerPlayer player, String itemType, String itemName, @Nullable Long index) {
        if (
            index != null &&
            Archipelago.archipelagoPersistentState.playerLastCheck.getOrDefault(player.getStringUUID(),0) >= index
        ) {
            return;
        }
        if (index != null) {
            Archipelago.archipelagoPersistentState.playerLastCheck.put(player.getStringUUID(), index.intValue());
        }

        switch (itemType) {
            case "adv":
                if (Utils.isAdvancementId(itemName)) {
                    Archipelago.archipelagoPersistentState.advancementChecks.put(itemName, true);
                    Advancement advancement = Archipelago.server.getAdvancements().getAdvancement(new ResourceLocation(itemName));
                    PlayerAdvancementAccessor playerAdvancements = ((PlayerAdvancementAccessor)Archipelago.server.getPlayerList().getPlayerAdvancements(player));
                    playerAdvancements.archipelago$markForVisibilityUpdate(advancement);
                    if (Archipelago.slotData.isInitiated && Archipelago.slotData.advancement_checks_give_items) {
                        assert advancement != null; // via isAdvancementId
                        DisplayInfo display = advancement.getDisplay();
                        if (display != null) {
                            Utils.giveItem(player, display.getIcon().getItem());
                        }
                    }
                }
                break;
            case "ftb":
                break;
            case "item":
                if (Utils.isItemId(itemName)) {
                    Utils.giveItem(player, itemName);
                }
                break;
        }
    }
}
