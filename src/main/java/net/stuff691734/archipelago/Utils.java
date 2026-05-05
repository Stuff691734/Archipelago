package net.stuff691734.archipelago;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.registries.ForgeRegistries;


import javax.annotation.Nullable;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        if (Archipelago.getServer() == null) {
            return false;
        }
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.getServer().getAdvancementManager().getAdvancement(id);
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

    public static void giveItem(EntityPlayerMP player, Item item, int amount) {
        ItemStack itemStack = new ItemStack(item, amount);
        if (!player.inventory.addItemStackToInventory(itemStack)) {
            player.entityDropItem(itemStack);
        }
    }

    public static void giveItem(MinecraftServer server, String item, @Nullable Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String itemName = strings[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
        if (itemValue != null) {
            giveItem(server, itemValue, amount, index);
        }
    }

    public static void giveItem(MinecraftServer server, Item item, @Nullable Long index) {
        giveItem(server, item, 1, index);
    }

    public static void giveItem(MinecraftServer server, Item item, int amount, @Nullable Long index) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance() != null) {
                    if (ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0) < index) {
                        giveItem(player, item, amount);
                    }
                }
            } else {
                giveItem(player, item, amount);
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

    public static void sendMessage(TextComponentString message) {
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message);
            }
        });
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

            return !ArchipelagoPersistentState.getAdvancement(rootAdvancementName);
        }
        // parent advancement
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (Utils.getRoot(advancement) == advancement) {
                // if root check against self
                return !ArchipelagoPersistentState.getAdvancement(advancement.getId().toString());
            } else {
                // otherwise check against values up tree not including self
                Advancement checkAdvancement = advancement;
                // exits when all advancements up the tree have been checked
                while (checkAdvancement != null) {
                    checkAdvancement = checkAdvancement.getParent();

                    if (checkAdvancement != null) {
                        String checkAdvancementName = checkAdvancement.getId().toString();
                        if (!ArchipelagoPersistentState.getAdvancement(checkAdvancementName)) {
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
            return !ArchipelagoPersistentState.getAdvancement(advancement.getId().toString());
        }
    }
}
