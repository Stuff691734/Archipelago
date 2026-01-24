package net.stuff691734.archipelago;

import net.minecraft.advancement.Advancement;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Utils {
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            String namespace = advancementId.split(":")[0];
            String path = advancementId.split(":")[1];
            Advancement advancement = Archipelago.server.getAdvancementManager().get(new Identifier(namespace, path));
            assert advancement != null;
            return advancement == getRoot(advancement);
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        Identifier id;
        try {
            id = new Identifier(advancementId);
        } catch (InvalidIdentifierException exception) {
            return false;
        }
        Advancement advancement = Archipelago.server.getAdvancementManager().get(id);
        return advancement != null;
    }

    public static void giveItem(ServerPlayerEntity player, String item) {
        String[] strings = item.split(" ");
        int amount = Integer.parseInt(strings[0]);
        String namespace = strings[1].split(":")[0];
        String path = strings[1].split(":")[1];
        ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(namespace, path)), amount);
        if (!player.giveItemStack(itemStack)) {
            player.dropStack(itemStack);
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
        Archipelago.server.sendMessage(message);

        for(ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
            player.sendMessage(message);
        }
    }
}
