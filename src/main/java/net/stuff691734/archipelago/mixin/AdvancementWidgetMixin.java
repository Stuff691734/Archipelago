package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow
    @Nullable
    private AdvancementWidget parent;

    @Shadow
    @Final
    private DisplayInfo display;

    @Shadow
    @Final
    private Advancement advancement;

    @Redirect(method = "drawConnectivity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;parent:Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;", opcode = Opcodes.GETFIELD))
    public AdvancementWidget parent(AdvancementWidget thisWidget) {
        return Utils.shouldAdvancementBeHidden(this.display, this.advancement) ? null : this.parent;
    }
}
