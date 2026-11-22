package net.stuff691734.archipelago.mixin.betteradvancements;

import betteradvancements.common.gui.BetterAdvancementTab;
import betteradvancements.common.gui.BetterAdvancementsScreen;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BetterAdvancementsScreen.class)
public class BetterAdvancementsScreenMixin {
    @Redirect(method = "onAddAdvancementRoot", at = @At(value = "INVOKE", target = "Lbetteradvancements/common/gui/BetterAdvancementTab;create(Lnet/minecraft/client/Minecraft;Lbetteradvancements/common/gui/BetterAdvancementsScreen;ILnet/minecraft/advancements/AdvancementNode;II)Lbetteradvancements/common/gui/BetterAdvancementTab;"))
    public BetterAdvancementTab avoidAddingEmptyPages(Minecraft minecraft, BetterAdvancementsScreen screen, int index, AdvancementNode advancementNode, int width, int height) {
        return Archipelago.logic.isTabDrawn(
                BetterAdvancementTab.create(minecraft, screen, index, advancementNode, width, height),
                new AdvancementImpl(advancementNode)
        );
    }
}
