package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ChecksState;
import net.stuff691734.archipelago.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.getDisplay() != null && this.getOrStartProgress(advancement).isDone()) {
            if (Archipelago.client.isConnected()) {
                Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(advancement.getId().toString());
                if (advancement_id != null) {
                    Archipelago.client.getLocationManager().checkLocation(advancement_id);
                    ChecksState checksState = ChecksState.getServerState(Archipelago.server);
                    if (advancement.getId().toString().equals(checksState.slotData.get("final_goal"))) {
                        Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                    }
                }
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
            ChecksState checksState = ChecksState.getServerState(Archipelago.server);

            if (Objects.equals(checksState.slotData.get("unlock_type"), "tab")) {
                Advancement rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                if (!checksState.checks.getOrDefault(rootAdvancementName, false)) {
                    // if player hasn't received root check prevent them from getting the advancement
                    cir.setReturnValue(false);
                }
            }
            // parent advancement
            else if (Objects.equals(checksState.slotData.get("unlock_type"), "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
                    // if root check against self
                    if (!checksState.checks.getOrDefault(advancement.getId().toString(), false)) {
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
                cir.setReturnValue(checksState.checks.getOrDefault(advancement.getId().toString(), false));
            }
        }
    }
}