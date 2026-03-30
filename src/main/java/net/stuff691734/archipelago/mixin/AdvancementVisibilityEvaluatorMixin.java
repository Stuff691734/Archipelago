
package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(AdvancementVisibilityEvaluator.class)
public class AdvancementVisibilityEvaluatorMixin {
    @Redirect(method = "evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean alwaysDisplay(Predicate<AdvancementNode> instance, Object t) {
        if (((AdvancementNode) t).advancement().display().isPresent()) {
            if (Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(((AdvancementNode) t).advancement().display().get().getType().getSerializedName())
                )
            ) {
                return instance.test((AdvancementNode) t);
            }
            return true;
        }
        return instance.test((AdvancementNode) t);
    }
}
