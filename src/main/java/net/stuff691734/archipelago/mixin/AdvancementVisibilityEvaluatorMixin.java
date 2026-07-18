
package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(AdvancementVisibilityEvaluator.class)
public class AdvancementVisibilityEvaluatorMixin {
    @Redirect(method = "evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static <T> boolean alwaysDisplay(Predicate<T> instance, T t) {
        return Archipelago.logic.shouldShowAdvancement(new AdvancementImpl((AdvancementNode) t)) || instance.test(t);
    }
}
