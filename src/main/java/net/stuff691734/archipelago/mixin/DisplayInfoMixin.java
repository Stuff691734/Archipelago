package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.stuff691734.archipelago.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementEntryGui.class)
public class DisplayInfoMixin {
    @Shadow
    @Final
    private Advancement advancement;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean drawSetNotHidden(DisplayInfo display) {
        return Utils.shouldAdvancementBeHidden(display, this.advancement);
    }

    @Redirect(method = "isMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean isMouseOverSetNotHidden(DisplayInfo display) {
        return Utils.shouldAdvancementBeHidden(display, this.advancement);
    }
}
