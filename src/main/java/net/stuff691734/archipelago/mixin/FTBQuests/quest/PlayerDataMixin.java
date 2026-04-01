package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.DependencyRequirement;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.reward.RewardClaimType;
import com.feed_the_beast.ftbquests.quest.task.Task;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerData.class)
public class PlayerDataMixin {
    @Inject(
            method = "areDependenciesComplete",
            at = @At(value = "RETURN"),
            cancellable = true,
            remap = false
    )
    private void preventTaskCompletion(Quest quest, CallbackInfoReturnable<Boolean> cir) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            // modules or shapes being null means not initialized -> show dependency
            // not randomizing ftb quests or not randomizing this type of quest
            return;
        }

        // prevents quests from being unlocked
        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                cir.setReturnValue(false);
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            DependencyRequirement requirement = quest.dependencyRequirement;
            if (quest.dependencies.isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                // no dependencies, check if it has self
                cir.setReturnValue(false);
            }
            if (requirement.one) {
                if (quest.dependencies.stream()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need one dependency, check if it has any
                    cir.setReturnValue(false);
                }
            } else {
                if (!quest.dependencies.stream()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need all dependency, check if it has all
                    cir.setReturnValue(false);
                }
            }
        }
        else {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getClaimType", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void preventRewardAccess(Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (!cir.getReturnValue().isClaimed() && Archipelago.slotData.isFTBQuestRewardRandomized(reward.quest.getShape())) {
            if (Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(reward.quest.getCodeString(), false)) {
                cir.setReturnValue(RewardClaimType.CAN_CLAIM);
            } else {
                cir.setReturnValue(RewardClaimType.CANT_CLAIM);
            }
        }
    }
}
