package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
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
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            String namespace = advancementId.split(":")[0];
            String path = advancementId.split(":")[1];
            Advancement advancement = Archipelago.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(namespace, path));
            assert advancement != null;
            return advancement == getRoot(advancement);
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent(identifier -> {
            Advancement advancement = Archipelago.server.getAdvancements().getAdvancement(identifier);
            result.set(advancement != null);
        });
        return result.get();
    }

    public static void giveItem(ServerPlayer player, String item) {
        String[] strings = item.split(" ");
        int amount = Integer.parseInt(strings[0]);
        String namespace = strings[1].split(":")[0];
        String path = strings[1].split(":")[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
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
