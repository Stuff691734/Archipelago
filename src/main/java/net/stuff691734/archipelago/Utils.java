package net.stuff691734.archipelago;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.registries.ForgeRegistries;


import java.util.UUID;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.server.getAdvancementManager().getAdvancement(id);
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

    public static void giveItem(EntityPlayerMP player, String itemId) {
        String[] strings = itemId.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String item = strings[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
        if (itemValue != null) {
            giveItem(player, itemValue, amount);
        }
    }

    public static void giveItem(ServerPlayerEntity player, Item item) {
        giveItem(player, item, 1);
    }

    public static void giveItem(ServerPlayerEntity player, Item item, int amount) {
        ItemStack itemStack = new ItemStack(item, amount);
        if (!player.inventory.addItemStackToInventory(itemStack)) {
            player.entityDropItem(itemStack);
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

    public static void sendMessage(TextComponentString message) {
        Archipelago.server.sendMessage(message);

        for(EntityPlayerMP player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendMessage(message);
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
            Advancement rootAdvancement = Utils.getRoot(advancement);
            String rootAdvancementName = rootAdvancement.getId().toString();

            return !Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false);
        }
        // parent advancement
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (Utils.getRoot(advancement) == advancement) {
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
