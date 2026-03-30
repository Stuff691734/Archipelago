package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent(identifier -> {
            Advancement advancement = Archipelago.server.getAdvancements().getAdvancement(identifier);
            result.set(advancement != null);
        });
        return result.get();
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
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent((identifier) ->  result.set(ForgeRegistries.ITEMS.containsKey(identifier)));
        return result.get();
    }

    public static void giveItem(ServerPlayer player, String itemId) {
        String[] strings = itemId.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String item = strings[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(item));
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
        Archipelago.server.sendSystemMessage(message);

        for(ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }

    public static boolean shouldAdvancementBeHidden(DisplayInfo display, Advancement advancement) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("Advancements") ||
                !Archipelago.slotData.advancement_difficulty.contains(display.getFrame().getName())
            )
        ) {
            if (
                Archipelago.slotData.activated_modules.contains("Advancements") &&
                !Archipelago.slotData.advancement_difficulty.contains(display.getFrame().getName())
            ) {
                return true;
            }

            return display.isHidden();
        }

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            Advancement rootAdvancement = Advancement.getRoot(advancement);
            String rootAdvancementName = rootAdvancement.getId().toString();

            return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false);
        }
        // parent advancement
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (Advancement.getRoot(advancement) == advancement) {
                // if root check against self
                return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false);
            } else {
                // otherwise check against values up tree not including self
                Advancement checkAdvancement = advancement;
                // exits when all advancements up the tree have been checked
                while (checkAdvancement != null) {
                    checkAdvancement = checkAdvancement.getParent();

                    if (checkAdvancement != null) {
                        String checkAdvancementName = checkAdvancement.getId().toString();
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
            return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false);
        }
    }
}
