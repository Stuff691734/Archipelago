package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardClaimType;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(TeamData.class)
public class TeamDataMixin {
    @Inject(
            method = "canStartTasks",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void preventTaskCompletion(Quest quest, CallbackInfoReturnable<Boolean> cir) {
        if (!FTBQuestsMixinHelper.isQuestStartable(cir.getReturnValue(), quest)) {
            cir.setReturnValue(false);
        }
//        if (
//            Archipelago.slotData.isInitiated &&
//            (
//                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
//                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
//            )
//        ) {
//            // modules or shapes being null means not initialized -> show dependency
//            // not randomizing ftb quests or not randomizing this type of quest
//            return;
//        }
//
//        // prevents quests from being unlocked
//        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
//            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
//                // if player hasn't received quest chapter check prevent them from getting the advancement
//                cir.setReturnValue(false);
//            }
//        }
//        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
//            DependencyRequirement requirement = quest.dependencyRequirement;
//            if (quest.dependencies.isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
//                // no dependencies, check if it has self
//                cir.setReturnValue(false);
//            }
//            if (requirement.one) {
//                if (quest.dependencies.stream()
//                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
//                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
//                ) {
//                    // need one dependency, check if it has any
//                    cir.setReturnValue(false);
//                }
//            } else {
//                if (!quest.dependencies.stream()
//                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
//                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
//                ) {
//                    // need all dependency, check if it has all
//                    cir.setReturnValue(false);
//                }
//            }
//        }
//        else {
//            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
//                cir.setReturnValue(false);
//            }
//        }
    }

    @Inject(method = "getClaimType", at = @At(value = "RETURN"), cancellable = true)
    private void preventRewardAccess(UUID player, Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (!cir.getReturnValue().isClaimed() && Archipelago.slotData.isFTBQuestRewardRandomized(reward.getQuest().getShape())) {
            if (ArchipelagoPersistentState.getCheck(reward.getQuest().getCodeString())) {
                cir.setReturnValue(RewardClaimType.CAN_CLAIM);
            } else {
                cir.setReturnValue(RewardClaimType.CANT_CLAIM);
            }
        }
    }
}
