package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import net.stuff691734.archipelagoLib.CheckType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Shadow
    @Final
    private Path playerSavePath;

    @Shadow
    private AdvancementTree tree;

    @Shadow
    public abstract void save();

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        // this calls other mixin to check if completable, so don't need to check here again
        if (this.getOrStartProgress(advancement).isDone()) {
            Archipelago.client.sendCheck(CheckType.ADVANCEMENT.addPrefix(advancement.id().toString()));
        }
    }

    @Inject(
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!Archipelago.logic.isAdvancementCompletable(new AdvancementImpl(this.tree.get(advancement)))) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "applyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements$Data;forEach(Ljava/util/function/BiConsumer;)V"))
    private void forEach(@Coerce DataMixin instance, BiConsumer<ResourceLocation, AdvancementProgress> action) {
        if (Archipelago.getServer() != null) {
            Map<ResourceLocation, AdvancementProgress> map = new HashMap<>();

            instance.archipelago$forEach(map::put);
            Archipelago.getServer().getAdvancements().getAllAdvancements().forEach((advancement) -> {
                map.putIfAbsent(advancement.id(), this.getOrStartProgress(advancement));
            });

            map.forEach(action);
        }
    }

    @Mixin(targets = "net.minecraft.server.PlayerAdvancements$Data")
    public interface DataMixin {
        @Invoker("forEach")
        void archipelago$forEach(BiConsumer<ResourceLocation, AdvancementProgress> p_300973_);
    }

    @Inject(method = "load", at = @At(value = "HEAD"))
    public void loadAdvancementsOnFirstJoin(ServerAdvancementManager p_240921_1_, CallbackInfo ci) {
        if (!Files.isRegularFile(this.playerSavePath)) {
            this.save();
        }
    }
}