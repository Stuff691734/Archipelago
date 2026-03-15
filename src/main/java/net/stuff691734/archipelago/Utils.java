package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

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

    public static void giveItem(MinecraftServer server, String item, long index) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // make sure user hasn't already gotten item
            if (Archipelago.archipelagoPersistentState.playerLastCheck.getOrDefault(player.getStringUUID(),0) < index) {
                Archipelago.archipelagoPersistentState.playerLastCheck.put(player.getStringUUID(), (int) index);
                Archipelago.archipelagoPersistentState.setDirty();
                giveItem(player, item);
            }
        }
    }

    public static void giveItem(ServerPlayer player, String itemId) {
        String[] strings = itemId.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        String item = strings[1];
        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)), amount);
        if (!player.addItem(itemStack)) {
            player.spawnAtLocation(itemStack);
        }
    }

    public static void sendMessage(ServerPlayer player, Component message) {
        player.sendSystemMessage(message);
    }

    public static void sendMessage(Component message) {
        Archipelago.server.sendSystemMessage(message);

        for(ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }
}
