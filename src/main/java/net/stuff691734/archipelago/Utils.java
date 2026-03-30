package net.stuff691734.archipelago;

import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.server.getAdvancements().getAdvancement(id);
        return advancement != null;
    }

    public static boolean isItemId(String itemId) {
        String item;
        try {
            item = itemId.split(" ")[1];
        } catch (IndexOutOfBoundsException exception) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemId);
            return false;
        }
        ResourceLocation id;
        try {
            id = new ResourceLocation(item);
        } catch (ResourceLocationException exception) {
            return false;
        }
        return ForgeRegistries.ITEMS.containsKey(id);
    }

    public static void giveItem(ServerPlayer player, String itemId) {
        String[] strings = itemId.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String item = strings[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
        if (itemValue != null) {
            ItemStack itemStack = new ItemStack(itemValue, amount);
            if (!player.addItem(itemStack)) {
                player.spawnAtLocation(itemStack);
            }
        }
    }

    public static Advancement getRoot(Advancement advancement) {
        Advancement advancement1 = advancement;
        while (true) {
            Advancement advancement2 = advancement1.getParent();
            if (advancement2 == null) {
                return advancement1;
            }
            advancement1 = advancement2;
        }
    }

    public static void sendMessage(Component message) {
        Archipelago.server.sendMessage(message, UUID.randomUUID());

        for(ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendMessage(message, UUID.randomUUID());
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
