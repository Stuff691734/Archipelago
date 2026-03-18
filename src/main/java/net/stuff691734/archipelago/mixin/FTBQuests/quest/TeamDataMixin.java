package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardClaimType;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(TeamData.class)
public class TeamDataMixin {
    @Inject(
            method = "canStartTasks",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void preventTaskCompletion(Quest quest, CallbackInfoReturnable<Boolean> cir) {
        String modules = Archipelago.archipelagoPersistentState.slotData.get("activated_modules");
        String shapes = Archipelago.archipelagoPersistentState.slotData.get("ftb_quest_check_shape");

        if (
            modules != null &&
            shapes != null &&
            (!modules.contains("FTBQuests") || !shapes.contains(quest.getShape()))
        ) {
            // modules or shapes being null means not initialized -> show dependency
            // not randomizing ftb quests or not randomizing this type of quest
            return;
        }

        // prevents quests from being unlocked
        if (Objects.equals(Archipelago.archipelagoPersistentState.slotData.get("unlock_type"), "tab")) {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                cir.setReturnValue(false);
            }
        }
        else if (Objects.equals(Archipelago.archipelagoPersistentState.slotData.get("unlock_type"), "tree")) {
            QuestAccessor questAccessor = (QuestAccessor) (Object) quest;
            assert questAccessor != null;
            DependencyRequirement requirement = questAccessor.archipelago$getDependencyRequirement();
            if (quest.streamDependencies().findAny().isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                // no dependencies, check if it has self
                cir.setReturnValue(false);
            }
            if (requirement.needOnlyOne()) {
                if (quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need one dependency, check if it has any
                    cir.setReturnValue(false);
                }
            } else {
                if (!quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need all dependency, check if it has all
                    cir.setReturnValue(false);
                }
            }
        }
        // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
        // to do an advancement insanity thing
        else {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getClaimType", at = @At(value = "RETURN"), cancellable = true)
    private void preventRewardAccess(UUID player, Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (!cir.getReturnValue().isClaimed()) {
            if (Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(reward.getQuest().getCodeString(), false)) {
                cir.setReturnValue(RewardClaimType.CAN_CLAIM);
            } else {
                cir.setReturnValue(RewardClaimType.CANT_CLAIM);
            }
        }
    }
}
