package net.stuff691734.archipelago.mixin;


import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.allowAdvancementCompletion(advancement)) {
            MixinHelper.sendArchipelagoAdvancement(advancement);
        }
    }

    @Inject(
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!MixinHelper.allowAdvancementCompletion(advancement)) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void forEach(Stream<Map.Entry<ResourceLocation, AdvancementProgress>> instance, Consumer<Map.Entry<ResourceLocation, AdvancementProgress>> consumer) {
        if (Archipelago.getServer() == null) {
            return;
        }
        Map<ResourceLocation, AdvancementProgress> list = Archipelago.getServer().getAdvancements().getAllAdvancements().stream().map(
                (advancement) -> Map.entry(advancement.getId(), this.getOrStartProgress(advancement))).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2)->e1
                )
        );
        instance.forEach((entry) -> list.put(entry.getKey(), entry.getValue()));
        list.entrySet().forEach(consumer);
    }
}