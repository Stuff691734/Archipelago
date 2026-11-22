package net.stuff691734.archipelago.mixin.betteradvancements;

import betteradvancements.common.gui.BetterAdvancementWidget;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BetterAdvancementWidget.class)
public class BetterAdvancementWidgetMixin {
    @Shadow
    @Final
    private AdvancementNode advancementNode;

    @Shadow
    private BetterAdvancementWidget parent;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;hidden()Z"))
    public boolean drawSetNotHidden(DisplayInfo display) {
        return !Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(this.advancementNode));
    }

    @Redirect(method = "isMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;hidden()Z"))
    public boolean isMouseOverSetNotHidden(DisplayInfo display) {
        return !Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(this.advancementNode));
    }

    @Redirect(method = "drawConnectivity", at = @At(value = "FIELD", target = "Lbetteradvancements/common/gui/BetterAdvancementWidget;parent:Lbetteradvancements/common/gui/BetterAdvancementWidget;", opcode = Opcodes.GETFIELD))
    public BetterAdvancementWidget parent(BetterAdvancementWidget thisWidget) {
        return Archipelago.logic.isDependencyDrawn(this.parent, new AdvancementImpl(this.advancementNode));
    }
}
