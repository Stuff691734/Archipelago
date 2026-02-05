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
                if (Objects.equals(checksState.slotData.get("unlock_type"), "tab")) {
                    PlacedAdvancement rootAdvancement = placedAdvancement.getRoot();
                    String rootAdvancementName = rootAdvancement.getAdvancementEntry().id().toString();

                    if (!checksState.checks.getOrDefault(rootAdvancementName, false)) {
                        // if player hasn't received root check prevent them from getting the advancement
                        cir.setReturnValue(false);
                    }
                }
                // parent advancement
                else if (Objects.equals(checksState.slotData.get("unlock_type"), "tree")) {
                    if (placedAdvancement.getRoot() == placedAdvancement) {
                        // if root check against self
                        if (!checksState.checks.getOrDefault(placedAdvancement.getAdvancementEntry().id().toString(), false)) {
                            cir.setReturnValue(false);
                        }
                    } else {
                        // otherwise check against values up tree not including self
                        PlacedAdvancement checkAdvancement = placedAdvancement;
                        // exits when all advancements up the tree have been checked
                        while (checkAdvancement != null) {
                            checkAdvancement = checkAdvancement.getParent();

                            if (checkAdvancement != null) {
                                String checkAdvancementName = checkAdvancement.getAdvancementEntry().id().toString();
                                if (!checksState.checks.getOrDefault(checkAdvancementName, false)) {
                                    cir.setReturnValue(false);
                                }
                            }
                        }
                    }
                }
                // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
                // to do an advancement insanity thing
                else {
                    cir.setReturnValue(checksState.checks.getOrDefault(placedAdvancement.getAdvancementEntry().id().toString(), false));
                }
            }
        });
    }
}