package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ChecksState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getOrStartProgress(AdvancementHolder advancement);

    @Shadow
    private AdvancementTree tree;

    @Inject(
            method = "award",
            at = @At("RETURN")
    )
    private void sendArchipelagoAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.value().display().isPresent() && this.getOrStartProgress(advancement).isDone()) {
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
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementProgress;isDone()Z"),
            cancellable = true
    )
    private void preventAdvancement(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        advancement.value().display().ifPresent(display -> {
            ChecksState checksState = ChecksState.getServerState(Archipelago.server);
            AdvancementNode placedAdvancement = this.tree.get(advancement);

            if (placedAdvancement != null) {
                AdvancementHolder checkAdvancement = placedAdvancement.holder();
                // root advancement
                if (Objects.equals(checksState.slotData.get("unlock_type"), "tab")) {
                    checkAdvancement = AdvancementNode.getRoot(placedAdvancement).holder();
                }
                // parent advancement
                else if (Objects.equals(checksState.slotData.get("unlock_type"), "tree")) {
                    AdvancementNode parent = placedAdvancement.parent();
                    if (parent == null) {
                        parent = placedAdvancement;
                    }
                    checkAdvancement = parent.holder();
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