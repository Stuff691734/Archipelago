package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {
    @Redirect(method = "onAddAdvancementRoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;create(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;ILnet/minecraft/advancements/AdvancementNode;)Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;"))
    public AdvancementTab avoidAddingEmptyPages(Minecraft minecraft, AdvancementsScreen screen, int index, AdvancementNode advancementNode) {
        return MixinHelper.getGuiAdvancementTab(AdvancementTab.create(minecraft, screen, index, advancementNode));
//        AdvancementTabGui advancementTab = AdvancementTabGui.create(minecraft, screen, index, advancementNode);
//        if (advancementTab == null || (Archipelago.slotData.isInitiated && !Archipelago.slotData.activated_modules.contains("Advancements"))) {
//            return advancementTab;
//        }
//        return Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancementTab.getRootNode().holder().id().toString(), false) ? advancementTab : null;
    }
}
