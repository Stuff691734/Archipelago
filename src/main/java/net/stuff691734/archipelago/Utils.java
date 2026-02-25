package net.stuff691734.archipelago;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.UUID;

public class Utils {
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            String namespace = advancementId.split(":")[0];
            String path = advancementId.split(":")[1];
            Advancement advancement = Archipelago.server.getAdvancementManager().getAdvancement(new ResourceLocation(namespace, path));
            assert advancement != null;
            return advancement == getRoot(advancement);
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (Exception exception) {
            return false;
        }
        Advancement advancement = Archipelago.server.getAdvancementManager().getAdvancement(id);
        return advancement != null;
    }

    public static void giveItem(EntityPlayerMP player, String item) {
        String[] strings = item.split(" ");
        int amount = Integer.parseInt(strings[0]);
        String namespace = strings[1].split(":")[0];
        String path = strings[1].split(":")[1];
        Item itemValue = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path));
        if (itemValue != null) {
            ItemStack itemStack = new ItemStack(itemValue, amount);
//            boolean a = player.
            if (!player.inventory.addItemStackToInventory(itemStack)) {
                player.entityDropItem(itemStack, 0);
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
        Archipelago.server.sendMessage(message);

        for(EntityPlayerMP player : Archipelago.server.getPlayerList().getPlayers()) {
            player.sendMessage(message);
        }
    }
}
