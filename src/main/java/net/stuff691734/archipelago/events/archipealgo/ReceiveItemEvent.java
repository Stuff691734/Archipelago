package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;

import javax.annotation.Nullable;

public class ReceiveItemEvent {
    @ArchipelagoEventListener
    public void onReceiveItems(io.github.archipelagomw.events.ReceiveItemEvent event) {
        Utils.sendMessage(new StringTextComponent(String.format(
                "Received %s from %s (%s)",
                event.getItemName(),
                event.getPlayerName(),
                event.getLocationName()
        )));
        String[] itemName = event.getItemName().split(" ",2);

        ReceiveItemEvent.parseItem(itemName[0], itemName[1], event.getIndex());
    }

    public static void parseItem(String itemType, String itemName, @Nullable Long index) {
        if (Archipelago.getServer() != null && ArchipelagoPersistentState.getInstance() != null) {
            serverParseItem(Archipelago.getServer(), ArchipelagoPersistentState.getInstance(), itemType, itemName, index);
            ArchipelagoPersistentState.getInstance().setDirty(true);
        }
    }

    public static void serverParseItem(MinecraftServer server, ArchipelagoPersistentState state, String itemType, String itemName, @Nullable Long index) {
        switch (itemType) {
            case "adv":
                if (Utils.isAdvancementId(itemName)) {
                    state.advancementChecks.put(itemName, true);
                    Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(itemName));
                    for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                        ((PlayerAdvancementAccessor)server.getPlayerList().getPlayerAdvancements(player)).archipelago$ensureVisibility(advancement);
                    }
                    if (Archipelago.slotData.isInitiated && Archipelago.slotData.advancement_checks_give_items) {
                        assert advancement != null; // via isAdvancementId
                        DisplayInfo display = advancement.getDisplay();
                        if (display != null) {
                            Utils.giveItem(server, display.getIcon().getItem(), index);
                        }
                    }
                }
                break;
            case "ftb":
                break;
            case "item":
                if (Utils.isItemId(itemName)) {
                    Utils.giveItem(server, itemName, index);
                }
                break;
        }
        if (index != null) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                state.playerLastCheck.put(player.getCachedUniqueIdString(), index.intValue());
            }
        }
    }
}
