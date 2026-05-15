package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.mixin.DisplayInfoAccessor;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;
import net.stuff691734.archipelago.net.GetCheckPacket;

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
        String[] itemName = event.getItemName().split(" ",3);

        ReceiveItemEvent.parseItem(itemName[0], itemName[1], event.getIndex());
    }

    public static void parseItem(String itemType, String itemName, @Nullable Long index) {
        if (Archipelago.getServer() != null && ArchipelagoPersistentState.getInstance() != null) {
            serverParseItem(Archipelago.getServer(), ArchipelagoPersistentState.getInstance(), itemType, itemName, index);
            ArchipelagoPersistentState.getInstance().setDirty(true);
        }
    }

    public static void serverParseItem(MinecraftServer server, ArchipelagoPersistentState state, String itemType, String itemName, @Nullable Long index) {
        CheckType checkType = CheckType.getCheckType(itemType);
        switch (checkType) {
            case ADVANCEMENT:
                if (Utils.isAdvancementId(itemName)) {
                    state.checks.put(checkType.addPrefix(itemName), true);
                    Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(itemName));
                    for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                        ((PlayerAdvancementAccessor)server.getPlayerList().getPlayerAdvancements(player)).archipelago$ensureVisibility(advancement);
                        ArchipelagoPacketHandler.INSTANCE.sendTo(new GetCheckPacket(checkType.addPrefix(itemName)), player);
                    }
                    if (Archipelago.slotData.isInitiated && Archipelago.slotData.advancement_checks_give_items) {
                        assert advancement != null; // via isAdvancementId
                        DisplayInfo display = advancement.getDisplay();
                        if (display != null) {
                            Utils.giveItem(server, ((DisplayInfoAccessor) display).archipelago$getIcon().getItem(), index);
                        }
                    }
                }
                break;
            case FTB_QUEST:
                break;
            case ITEM:
                if (Utils.isItemId(itemName)) {
                    Utils.giveItem(server, itemName, index);
                }
                break;
            case DEFAULT:
                Archipelago.LOGGER.info(
                        "Received item: '{}' with type signature: '{}'. it did not match any known types",
                        itemName, itemType
                );
                break;
        }
        if (index != null) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                state.playerLastCheck.put(player.getCachedUniqueIdString(), index.intValue());
            }
        }
    }
}
