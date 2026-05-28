package net.stuff691734.archipelago.mixin;


import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Shadow
    private AdvancementTree tree;

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.allowAdvancementCompletion(advancement, this.tree.get(advancement))) {
            MixinHelper.sendArchipelagoAdvancement(advancement);
        }
    }

    @Inject(
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!MixinHelper.allowAdvancementCompletion(advancement, this.tree.get(advancement))) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "applyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements$Data;forEach(Ljava/util/function/BiConsumer;)V"))
    private void forEach(@Coerce DataMixin instance, BiConsumer<ResourceLocation, AdvancementProgress> action) {
        if (Archipelago.getServer() != null) {
            Map<ResourceLocation, AdvancementProgress> list = Archipelago.getServer().getAdvancements().getAllAdvancements().stream().map(
                    (advancement) -> Map.entry(advancement.id(), this.getOrStartProgress(advancement))).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1
                    )
            );
            instance.archipelago$forEach(list::put);
            list.forEach(action);
        }
    }

    @Mixin(targets = "net.minecraft.server.PlayerAdvancements$Data")
    public interface DataMixin {
        @Invoker("forEach")
        void archipelago$forEach(BiConsumer<ResourceLocation, AdvancementProgress> p_300973_);
    }
}