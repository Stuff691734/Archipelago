package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisplayInfo.class)
public interface DisplayInfoAccessor {
    @Accessor("icon")
    ItemStack archipelago$getIcon();
}
