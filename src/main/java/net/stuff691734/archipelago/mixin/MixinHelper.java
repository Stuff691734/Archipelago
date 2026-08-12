package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;

// methods used by coreMod to help write less bytecode
public class MixinHelper {
    public static boolean isHidden(DisplayInfo displayInfo, Advancement advancement) {
        if (Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(advancement))) {
            return false;
        }
        return displayInfo.isHidden();
    }
}
