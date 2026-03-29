package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftbquests.client.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(QuestButton.class)
public class OldQuestButtonMixin {
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;areDependenciesComplete(Ldev/ftb/mods/ftbquests/quest/Quest;)Z"), remap = false)
    public boolean drawAlertIcon(TeamData teamData, Quest quest) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            // modules or shapes being null means not initialized -> show dependency
            // not randomizing ftb quests or not randomizing this type of quest
            return teamData.areDependenciesComplete(quest);
        }

        // prevents quests from being unlocked
        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return false;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            QuestAccessor questAccessor = (QuestAccessor) (Object) quest;
            assert questAccessor != null;
            DependencyRequirement requirement = questAccessor.archipelago$getDependencyRequirement();
            if (quest.streamDependencies().findAny().isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                // no dependencies, check if it has self
                return false;
            }
            if (requirement.needOnlyOne()) {
                if (quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need one dependency, check if it has any
                    return false;
                }
            } else {
                if (!quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need all dependency, check if it has all
                    return false;
                }
            }
        }
        // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
        // to do an advancement insanity thing
        else {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                return false;
            }
        }
        return teamData.areDependenciesComplete(quest);
    }
}
