package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

public class Utils {
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            String namespace = advancementId.split(":")[0];
            String path = advancementId.split(":")[1];
            AdvancementEntry advancement = Archipelago.server.getAdvancementLoader().get(Identifier.of(namespace, path));
            assert advancement != null;
            return advancement.value().isRoot();
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        DataResult<Identifier> id = Identifier.validate(advancementId);
        AtomicBoolean result = new AtomicBoolean(false);
        id.result().ifPresent((identifier -> {
            AdvancementManager advancementManager = Archipelago.server.getAdvancementLoader().getManager();
            PlacedAdvancement advancement = advancementManager.get(identifier);
            result.set(advancement != null);
        }));
        return result.get();
    }

    public static void giveItem(ServerPlayerEntity player, String item) {
        String[] strings = item.split(" ");
        int amount = Integer.parseInt(strings[0]);
        String namespace = strings[1].split(":")[0];
        String path = strings[1].split(":")[1];
        ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.of(namespace, path)), amount);
        if (!player.giveItemStack(itemStack)) {
            player.dropStack(itemStack);
        }
    }

    public static void sendMessage(Text message) {
        Archipelago.server.sendMessage(message);

        for(ServerPlayerEntity player : Archipelago.server.getPlayerManager().getPlayerList()) {
            player.sendMessage(message);
        }
    }
}
