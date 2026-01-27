package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancement.*;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ChecksState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerAdvancementTracker.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getProgress(AdvancementEntry advancement);

    @Shadow
    private AdvancementManager advancementManager;

    @Inject(
            method = "grantCriterion",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.value().display().isPresent() && this.getProgress(advancement).isDone()) {
            if (Archipelago.client.isConnected()) {
                Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(advancement.id().toString());
                if (advancement_id != null) {
                    Archipelago.client.getLocationManager().checkLocation(advancement_id);
                    ChecksState checksState = ChecksState.getServerState(Archipelago.server);
                    if (advancement.id().toString().equals(checksState.slotData.get("final_goal"))) {
                        Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                    }
                }
            }
        }
    }

    @Inject(
        method = "grantCriterion",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementProgress;isDone()Z"),
        cancellable = true
    )
    private void preventAdvancement(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        advancement.value().display().ifPresent(display -> {
            ChecksState checksState = ChecksState.getServerState(Archipelago.server);
            PlacedAdvancement placedAdvancement = this.advancementManager.get(advancement);

            if (placedAdvancement != null) {
                AdvancementEntry checkAdvancement = placedAdvancement.getAdvancementEntry();
                // root advancement
                if (Objects.equals(checksState.slotData.get("unlock_type"), "tab")) {
                    checkAdvancement = placedAdvancement.getRoot().getAdvancementEntry();
                }
                // parent advancement
                else if (Objects.equals(checksState.slotData.get("unlock_type"), "tree")) {
                    PlacedAdvancement parent = placedAdvancement.getParent();
                    if (parent == null) {
                        parent = placedAdvancement;
                    }
                    checkAdvancement = parent.getAdvancementEntry();
                }

                String checkAdvancementName = checkAdvancement.id().toString();
                checkAdvancement.value().display().ifPresent(rootDisplay -> {
                    if (!checksState.checks.getOrDefault(checkAdvancementName, false)) {
                        // if player hasn't received necessary check prevent them from getting the advancement
                        cir.setReturnValue(false);
                    }
                });
            }
        });
    }
}