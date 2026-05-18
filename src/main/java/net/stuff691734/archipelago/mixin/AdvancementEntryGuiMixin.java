package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(AdvancementEntryGui.class)
public class AdvancementEntryGuiMixin {
    @Shadow
    @Final
    private Advancement advancement;

    @Shadow
    @Nullable
    private AdvancementEntryGui parent;

    @Shadow
    @Final
    private DisplayInfo display;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean drawSetNotHidden(DisplayInfo display) {
        return Utils.shouldAdvancementBeHidden(display, this.advancement);
    }

    @Redirect(method = "isMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean isMouseOverSetNotHidden(DisplayInfo display) {
        return Utils.shouldAdvancementBeHidden(display, this.advancement);
    }

    @Redirect(method = "drawConnectivity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/advancements/AdvancementEntryGui;parent:Lnet/minecraft/client/gui/advancements/AdvancementEntryGui;", opcode = Opcodes.GETFIELD))
    public AdvancementEntryGui parent(AdvancementEntryGui thisWidget) {
        return MixinHelper.getGuiAdvancementParent(this.parent, this.display, this.advancement);
    }
}
