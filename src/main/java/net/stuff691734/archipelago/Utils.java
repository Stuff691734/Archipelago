package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        if (id.isSuccess()) {
            AdvancementTree advancementManager = Archipelago.server.getAdvancements().tree();
            AdvancementNode advancement = advancementManager.get(id.getOrThrow());
            return advancement != null;
        }
        return false;
    }

    public static boolean isItemId(String itemId) {
        String item;
        try {
            item = itemId.split(" ")[1];
        } catch (IndexOutOfBoundsException exception) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemId);
            return false;
        }
        DataResult<ResourceLocation> id = ResourceLocation.read(item);
        if (id.isSuccess()) {
            return BuiltInRegistries.ITEM.containsKey(id.getOrThrow());
        }
        return false;
    }

    public static void giveItem(ServerPlayer player, String itemId) {
        String[] strings = itemId.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String item = strings[1];
        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(item)), amount);
        if (!player.addItem(itemStack)) {
            player.spawnAtLocation(itemStack);
        }
    }

    public static void sendMessage(Component message) {
        Archipelago.server.sendSystemMessage(message);

        for(ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }

    public static boolean shouldAdvancementBeHidden(DisplayInfo display, AdvancementNode advancement) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("Advancements") ||
                !Archipelago.slotData.advancement_difficulty.contains(display.getType().getSerializedName())
            )
        ) {
            if (
                Archipelago.slotData.activated_modules.contains("Advancements") &&
                !Archipelago.slotData.advancement_difficulty.contains(display.getType().getSerializedName())
            ) {
                return true;
            }

            return display.isHidden();
        }

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            AdvancementNode rootAdvancement = AdvancementNode.getRoot(advancement);
            String rootAdvancementName = rootAdvancement.holder().id().toString();

            return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false);
        }
        // parent advancement
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (AdvancementNode.getRoot(advancement) == advancement) {
                // if root check against self
                return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.holder().id().toString(), false);
            } else {
                // otherwise check against values up tree not including self
                AdvancementNode checkAdvancement = advancement;
                // exits when all advancements up the tree have been checked
                while (checkAdvancement != null) {
                    checkAdvancement = checkAdvancement.parent();

                    if (checkAdvancement != null) {
                        String checkAdvancementName = checkAdvancement.holder().id().toString();
                        if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkAdvancementName, false)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
        // to do an advancement insanity thing
        else {
            return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.holder().id().toString(), false);
        }
    }
}
