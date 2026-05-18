package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.item.ItemStack;
import net.stuff691734.archipelago.mixinHelper.DisplayInfoAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DisplayInfo.class)
public class DisplayInfoMixin implements DisplayInfoAccessor {
    @Shadow
    @Final
    private ItemStack icon;

    public ItemStack archipelago$getIcon() {
        return this.icon;
    };
}
