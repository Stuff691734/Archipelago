package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin {
    @Shadow
    @Final
    private Advancement advancement;

    @Shadow
    @Nullable
    private AdvancementWidget parent;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean drawSetNotHidden(DisplayInfo display) {
        return !Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(this.advancement));
    }

    @Redirect(method = "isMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;isHidden()Z"))
    public boolean isMouseOverSetNotHidden(DisplayInfo display) {
        return !Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(this.advancement));
    }

    @Redirect(method = "drawConnectivity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;parent:Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;", opcode = Opcodes.GETFIELD))
    public AdvancementWidget parent(AdvancementWidget thisWidget) {
        return Archipelago.logic.isDependencyDrawn(this.parent, new AdvancementImpl(this.advancement));
    }
}
