package net.stuff691734.archipelago;

import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
        DataResult<ResourceLocation> id = ResourceLocation.read(item);
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent((identifier) ->  result.set(ForgeRegistries.ITEMS.containsKey(identifier)));
        return result.get();
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
}
