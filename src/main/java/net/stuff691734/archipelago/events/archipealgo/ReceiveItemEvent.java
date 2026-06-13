package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;
import net.stuff691734.archipelago.net.GetCheckPacket;

import javax.annotation.Nullable;

public class ReceiveItemEvent {
    @ArchipelagoEventListener
    public void onReceiveItems(io.github.archipelagomw.events.ReceiveItemEvent event) {
        Utils.sendMessage(Component.literal(String.format(
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
        CheckType checkType = CheckType.getCheckType(itemType);
        switch (checkType) {
            case ADVANCEMENT:
                String advancementName = itemName.split(" ",2)[0];
                if (Utils.isAdvancementId(advancementName)) {
                    state.checks.put(checkType.addPrefix(advancementName), true);
                    AdvancementHolder advancement = server.getAdvancements().get(ResourceLocation.parse(advancementName));
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        ((PlayerAdvancementAccessor)server.getPlayerList().getPlayerAdvancements(player)).archipelago$markForVisibilityUpdate(advancement);
                    }
                    PacketDistributor.sendToAllPlayers(new GetCheckPacket(checkType.addPrefix(advancementName)));
                    if (Archipelago.slotData.isInitiated && Archipelago.slotData.advancement_checks_give_items) {
                        assert advancement != null; // via isAdvancementId
                        advancement.value().display().ifPresent(
                            display -> Utils.giveItem(server, display.getIcon(), index)
                        );
                    }
                }
                break;
            case FTB_QUEST:
                String questName = itemName.split(" ",2)[0];
                if (ModList.get().isLoaded("ftbquests") && FTBUtils.isQuestId(questName)) {
                    state.checks.put(checkType.addPrefix(questName), true);
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        FTBUtils.checkIsCompleted(player, itemName);
                    }
                    PacketDistributor.sendToAllPlayers(new GetCheckPacket(checkType.addPrefix(questName)));
                }
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
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                state.playerLastCheck.put(player.getStringUUID(), index.intValue());
            }
        }
    }
}
