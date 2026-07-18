package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {
    @Redirect(method = "onAddAdvancementRoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;create(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;ILnet/minecraft/advancements/AdvancementNode;)Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;"))
    public AdvancementTab avoidAddingEmptyPages(Minecraft minecraft, AdvancementsScreen screen, int index, AdvancementNode advancementNode) {
        return Archipelago.logic.isTabDrawn(
                AdvancementTab.create(minecraft, screen, index, advancementNode),
                new AdvancementImpl(advancementNode)
        );
    }
}
