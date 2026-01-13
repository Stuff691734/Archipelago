package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
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
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Inject(
            method = "grantCriterion",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.getDisplay() != null && this.getProgress(advancement).isDone()) {
            if (Archipelago.client.isConnected()) {
                // this does not collect the right ids
                Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(advancement.getId().toString());
                Archipelago.client.getLocationManager().checkLocation(advancement_id);
                ChecksState checksState = ChecksState.getServerState(Archipelago.server);
                if (advancement.getId().toString().equals(checksState.slotData.get("final_goal"))) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        }
    }

    @Inject(
        method = "grantCriterion",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementProgress;isDone()Z"),
        cancellable = true
    )
    private void preventAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        AdvancementDisplay display = advancement.getDisplay();
        if (display != null) {
            ChecksState checksState = ChecksState.getServerState(Archipelago.server);

            Advancement checkAdvancement = advancement;
            // root advancement
            if (Objects.equals(checksState.slotData.get("unlock_type"), "tab")) {
                checkAdvancement = advancement.getRoot();
            }
            // parent advancement
            else if (Objects.equals(checksState.slotData.get("unlock_type"), "tree")) {
                Advancement parent = advancement.getParent();
                if (parent != null) {
                    checkAdvancement = parent;
                }
            }

            String checkAdvancementName = checkAdvancement.getId().toString();
            AdvancementDisplay rootDisplay = checkAdvancement.getDisplay();
            if (rootDisplay != null) {
                if (!checksState.checks.getOrDefault(checkAdvancementName, false)) {
                    // if player hasn't received necessary check prevent them from getting the advancement
                    cir.setReturnValue(false);
                }
            }
        }
    }
}