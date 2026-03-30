package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.BiConsumer;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

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
                Advancement rootAdvancement = Advancement.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false)) {
                    // if player hasn't received root check prevent them from getting the advancement
                    cir.setReturnValue(false);
                }
            }
            // parent advancement
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Advancement.getRoot(advancement) == advancement) {
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

    @Redirect(method = "applyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerAdvancements$Data;forEach(Ljava/util/function/BiConsumer;)V"))
    private void forEach(@Coerce Object instance, BiConsumer<ResourceLocation, AdvancementProgress> action) {
        Archipelago.server.getAdvancements().getAllAdvancements().forEach((advancementHolder) -> {action.accept(advancementHolder.getId(), this.getOrStartProgress(advancementHolder));Archipelago.LOGGER.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");});
    }
}