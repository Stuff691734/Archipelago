package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class Utils {
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            String namespace = advancementId.split(":")[0];
            String path = advancementId.split(":")[1];
            AdvancementHolder advancement = Archipelago.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(namespace, path));
            assert advancement != null;
            return advancement.value().isRoot();
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        if (id.isSuccess()) {
            AdvancementTree advancementManager = Archipelago.server.getAdvancements().tree();
            AdvancementNode advancement = advancementManager.get(id.getOrThrow());
            return advancement != null;
        }
        return false;
    }

    public static void giveItem(ServerPlayer player, String item) {
        String[] strings = item.split(" ");
        int amount = Integer.parseInt(strings[0]);
        String namespace = strings[1].split(":")[0];
        String path = strings[1].split(":")[1];
        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path)), amount);
        if (!player.addItem(itemStack)) {
            player.spawnAtLocation(Archipelago.server.overworld(), itemStack);
        }
    }

    public static void sendMessage(Component message) {
        Archipelago.server.sendSystemMessage(message);

        for(ServerPlayer player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }
}
