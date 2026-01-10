package net.stuff691734.archipelago;

import com.mojang.serialization.DataResult;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.util.Identifier;

public class Utils {
    public static boolean isRootAdvancementId(String advancementId) {
        if (isAdvancementId(advancementId)) {
            AdvancementEntry advancement = Archipelago.server.getAdvancementLoader().get(Identifier.of(advancementId));
            assert advancement != null;
            return advancement.value().isRoot();
        }
        return false;
    }

    public static boolean isAdvancementId(String advancementId) {
        DataResult<Identifier> id = Identifier.validate(advancementId);
        if (id.isSuccess()) {
            AdvancementManager advancementManager = Archipelago.server.getAdvancementLoader().getManager();
            PlacedAdvancement advancement = advancementManager.get(id.getOrThrow());
            return advancement != null;
        }
        return false;
    }
}
