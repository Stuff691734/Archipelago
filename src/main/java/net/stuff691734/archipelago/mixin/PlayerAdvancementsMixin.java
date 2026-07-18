package net.stuff691734.archipelago.mixin;


import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.util.ResourceLocation;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import net.stuff691734.archipelagoLib.CheckType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Map;
import java.util.Set;

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

    @Shadow
    @Final
    private File progressFile;

    @Shadow
    public abstract void save();

    @Inject(
            method = "grantCriterion",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        // this calls other mixin to check if completable, so don't need to check here again
        if (this.getProgress(advancement).isDone()) {
            Archipelago.client.sendCheck(CheckType.ADVANCEMENT.addPrefix(advancement.getId().toString()));
        }
    }

    @Inject(
            method = "grantCriterion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!Archipelago.logic.isAdvancementCompletable(new AdvancementImpl(advancement))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldBeVisible", at = @At(value = "HEAD"), cancellable = true)
    public void shouldBeVisible(Advancement advancement, CallbackInfoReturnable<Boolean> cir) {
        if (Archipelago.logic.shouldShowAdvancement(new AdvancementImpl(advancement))) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private Set<Map.Entry<ResourceLocation, AdvancementProgress>> addAllAdvancementsToRender(Map<ResourceLocation, AdvancementProgress> instance) {
        if (Archipelago.getServer() != null) {
            Archipelago.getServer().getAdvancementManager().getAllAdvancements().forEach((advancement) -> {
                instance.putIfAbsent(advancement.getId(), this.getProgress(advancement));
            });
        }
        return instance.entrySet();
    }

    @Inject(method = "load", at = @At(value = "HEAD"))
    public void loadAdvancementsOnFirstJoin(CallbackInfo ci) {
        if (!this.progressFile.isFile()) {
            this.save();
        }
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/PlayerAdvancements;startProgress(Lnet/minecraft/advancements/Advancement;Lnet/minecraft/advancements/AdvancementProgress;)V"))
    public void showThings(PlayerAdvancements instance, Advancement advancement, AdvancementProgress progress) {
        this.startProgress(advancement, progress);
        this.progressChanged.add(advancement);
        this.ensureVisibility(advancement);
    }
}