package net.stuff691734.archipelago.mixin;


import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.server.PlayerAdvancements;
import net.stuff691734.archipelago.Archipelago;
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
        if (this.getOrStartProgress(advancement).isDone()) {
            if (Archipelago.client.isConnected()) {
                Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("adv " + advancement.id());
                if (advancement_id != null) {
                    Archipelago.client.getLocationManager().checkLocation(advancement_id);
                    if (("adv " + advancement.id()).equals(Archipelago.slotData.final_goal)) {
                        Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                    }
                }
            } else {
                Archipelago.archipelagoPersistentState.pendingChecks.add("adv " + advancement.id());
                Archipelago.archipelagoPersistentState.setDirty();
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
            if (
                Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.ftb_quest_shape.contains(display.getFrame().getName())
                )
            ) {
                return;
            }

            AdvancementNode placedAdvancement = this.tree.get(advancement);

            if (placedAdvancement != null) {
                if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                    AdvancementNode rootAdvancement = AdvancementNode.getRoot(placedAdvancement);
                    String rootAdvancementName = rootAdvancement.holder().id().toString();

                    if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false)) {
                        // if player hasn't received root check prevent them from getting the advancement
                        cir.setReturnValue(false);
                    }
                }
                // parent advancement
                else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                    if (AdvancementNode.getRoot(placedAdvancement) == placedAdvancement) {
                        // if root check against self
                        if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(placedAdvancement.holder().id().toString(), false)) {
                            cir.setReturnValue(false);
                        }
                    } else {
                        // otherwise check against values up tree not including self
                        AdvancementNode checkAdvancement = placedAdvancement;
                        // exits when all advancements up the tree have been checked
                        while (checkAdvancement != null) {
                            checkAdvancement = checkAdvancement.parent();

                            if (checkAdvancement != null) {
                                String checkAdvancementName = checkAdvancement.holder().id().toString();
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
                    cir.setReturnValue(Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(placedAdvancement.holder().id().toString(), false));
                }
            }
        });
    }
}