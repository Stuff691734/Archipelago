
package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(AdvancementVisibilityEvaluator.class)
public class AdvancementVisibilityEvaluatorMixin {
    @Redirect(method = "evaluateVisibility(Lnet/minecraft/advancements/Advancement;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean alwaysDisplay(Predicate<Advancement> instance, Object t) {
        if (((Advancement) t).getDisplay() != null) {
            if (Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(((Advancement) t).getDisplay().getFrame().getName())
                )
            ) {
                return instance.test((Advancement) t);
            }
            return true;
        }
        return instance.test((Advancement) t);
    }
}
