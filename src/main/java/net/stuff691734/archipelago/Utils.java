package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent(identifier -> {
            AdvancementTree advancementManager = Archipelago.server.getAdvancements().tree();
            AdvancementNode advancement = advancementManager.get(identifier);
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
        id.result().ifPresent((identifier) ->  result.set(BuiltInRegistries.ITEM.containsKey(identifier)));
        return result.get();
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
}
