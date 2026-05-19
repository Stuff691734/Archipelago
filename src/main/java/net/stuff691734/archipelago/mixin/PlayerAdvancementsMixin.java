package net.stuff691734.archipelago.mixin;


import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.util.ResourceLocation;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.MixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    @Final
    private Set<Advancement> progressChanged;

    @Shadow
    protected abstract void ensureVisibility(Advancement p_136011_);

    @Shadow
    protected abstract void startProgress(Advancement p_135986_, AdvancementProgress p_135987_);

    @Inject(
            method = "grantCriterion",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.allowAdvancementCompletion(advancement)) {
            MixinHelper.sendArchipelagoAdvancement(advancement);
        }
    }

    @Inject(
            method = "grantCriterion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!MixinHelper.allowAdvancementCompletion(advancement)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldBeVisible", at = @At(value = "HEAD"), cancellable = true)
    public void shouldBeVisible(Advancement advancement, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHelper.shouldBeVisible(advancement)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private Object forEach(Stream<Map.Entry<ResourceLocation, AdvancementProgress>> instance, Collector<Map.Entry<ResourceLocation, AdvancementProgress>, ?, List<Map.Entry<ResourceLocation, AdvancementProgress>>> arCollector) {
        if (Archipelago.getServer() != null) {
            Map<ResourceLocation, AdvancementProgress> list = Archipelago.getServer().getAdvancementManager().getAllAdvancements().stream().map(
                    (advancement) -> new AbstractMap.SimpleImmutableEntry<>(advancement.getId(), this.getProgress(advancement))).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1
                    )
            );
            instance.forEach((entry) -> list.put(entry.getKey(), entry.getValue()));
            return new ArrayList<>(list.entrySet());
        }
        return instance.collect(arCollector);
    }

    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/PlayerAdvancements;startProgress(Lnet/minecraft/advancements/Advancement;Lnet/minecraft/advancements/AdvancementProgress;)V"))
    public void showThings(PlayerAdvancements instance, Advancement advancement, AdvancementProgress progress) {
        this.startProgress(advancement, progress);
        this.progressChanged.add(advancement);
        this.ensureVisibility(advancement);
    }
}