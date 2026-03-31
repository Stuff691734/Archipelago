package net.stuff691734.archipelago.mixin;


import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Dynamic;
import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

    @Shadow
    @Final
    private Set<Advancement> progressChanged;

    @Shadow
    protected abstract void ensureVisibility(Advancement p_136011_);

    @Shadow
    protected abstract void startProgress(Advancement p_135986_, AdvancementProgress p_135987_);

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (this.getOrStartProgress(advancement).isDone()) {
            if (Archipelago.client.isConnected()) {
                Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("adv " + advancement.getId());
                if (advancement_id != null) {
                    Archipelago.client.getLocationManager().checkLocation(advancement_id);
                    if (("adv " + advancement.getId()).equals(Archipelago.slotData.final_goal)) {
                        Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                    }
                }
            } else {
                Archipelago.archipelagoPersistentState.pendingChecks.add("adv " + advancement.getId());
                Archipelago.archipelagoPersistentState.setDirty();
            }
        }
    }

    @Inject(
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        DisplayInfo display = advancement.getDisplay();
        if (display != null) {
            if (
                Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(display.getFrame().getName())
                )
            ) {
                return;
            }

            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                Advancement rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false)) {
                    // if player hasn't received root check prevent them from getting the advancement
                    cir.setReturnValue(false);
                }
            }
            // parent advancement
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
                    // if root check against self
                    if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false)) {
                        cir.setReturnValue(false);
                    }
                } else {
                    // otherwise check against values up tree not including self
                    Advancement checkAdvancement = advancement;
                    // exits when all advancements up the tree have been checked
                    while (checkAdvancement != null) {
                        checkAdvancement = checkAdvancement.getParent();

                        if (checkAdvancement != null) {
                            String checkAdvancementName = checkAdvancement.getId().toString();
                            if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkAdvancementName, false)) {
                                cir.setReturnValue(false);
                            }
                        }
                    }
                }
            }
            // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
            // to do an advancement insanity thing
            else {
                cir.setReturnValue(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false));
            }
        }
    }

    @Inject(method = "shouldBeVisible", at = @At(value = "HEAD"), cancellable = true)
    public void shouldBeVisible(Advancement advancement, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.getDisplay() != null) {
            if (Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(advancement.getDisplay().getFrame().getName())
                )
            ) {
                return;
            }
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private Object forEach(Stream<Map.Entry<ResourceLocation, AdvancementProgress>> instance, Collector<Map.Entry<ResourceLocation, AdvancementProgress>, ?, List<Map.Entry<ResourceLocation, AdvancementProgress>>> arCollector) {
        Map<ResourceLocation, AdvancementProgress> list = Archipelago.server.getAdvancements().getAllAdvancements().stream().map(
            (advancement) -> Map.entry(advancement.getId(), this.getOrStartProgress(advancement))).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2)->e1
            )
        );
        instance.forEach((entry) -> list.put(entry.getKey(), entry.getValue()));
        return new ArrayList<>(list.entrySet());
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements;startProgress(Lnet/minecraft/advancements/Advancement;Lnet/minecraft/advancements/AdvancementProgress;)V"))
    public void showThings(PlayerAdvancements instance, Advancement advancement, AdvancementProgress progress) {
        this.startProgress(advancement, progress);
        this.progressChanged.add(advancement);
        this.ensureVisibility(advancement);
    }
}