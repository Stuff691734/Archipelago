package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.stuff691734.archipelago.Utils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(AdvancementEntryGui.class)
public abstract class AdvancementWidgetMixin {
    @Shadow
    @Nullable
    private AdvancementEntryGui parent;

    @Shadow
    @Final
    private DisplayInfo displayInfo;

    @Shadow
    @Final
    private Advancement advancement;

    @Redirect(method = "drawConnectionLineToParent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/advancements/AdvancementEntryGui;parent:Lnet/minecraft/client/gui/advancements/AdvancementEntryGui;", opcode = Opcodes.GETFIELD))
    public AdvancementEntryGui parent(AdvancementEntryGui thisWidget) {
        return Utils.shouldAdvancementBeHidden(this.displayInfo, this.advancement) ? null : this.parent;
    }
}
