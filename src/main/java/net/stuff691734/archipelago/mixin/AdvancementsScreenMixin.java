package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {
    @Redirect(method = "rootAdvancementAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/advancements/AdvancementTabGui;create(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/advancements/AdvancementsScreen;ILnet/minecraft/advancements/Advancement;)Lnet/minecraft/client/gui/advancements/AdvancementTabGui;"))
    public AdvancementTabGui avoidAddingEmptyPages(Minecraft minecraft, AdvancementsScreen screen, int index, Advancement advancementNode) {
        return MixinHelper.getGuiAdvancementTab(AdvancementTabGui.create(minecraft, screen, index, advancementNode));
    }
}
