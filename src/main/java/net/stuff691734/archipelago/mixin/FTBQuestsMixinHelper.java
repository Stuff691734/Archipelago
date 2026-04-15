package net.stuff691734.archipelago.mixin;

import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftbquests.client.ClientQuestData;
import com.feed_the_beast.ftbquests.quest.DependencyRequirement;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import io.github.archipelagomw.ClientStatus;
import net.stuff691734.archipelago.Archipelago;

import java.util.Objects;

// used by class transformers, even though it appears to be unused
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, ClientQuestData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            if (
                    Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false) &&
                            quest.rewards.stream().anyMatch(
                                    reward -> !data.isRewardClaimedSelf(reward)
                            )
            ) {
                // got this check but haven't claimed yet
                return ThemeProperties.ALERT_ICON.get(quest);
            }
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : Icon.EMPTY;
    }

    public static boolean isQuestRewardAvailable(Quest quest, QuestData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false);
        }
        return quest.isComplete(data);
    }

    public static void sendArchipelagoQuest(Quest quest) {
        Archipelago.LOGGER.info("Quest Completed.");
        if (Archipelago.client.isConnected()) {
            Long quest_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("ftb " + quest);
            if (quest_id != null) {
                Archipelago.client.getLocationManager().checkLocation(quest_id);
                if (("ftb " + quest).equals(Archipelago.slotData.final_goal)) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else {
            Archipelago.archipelagoPersistentState.pendingChecks.add("ftb " + quest);
            Archipelago.archipelagoPersistentState.setDirty(true);
        }
    }

    public static boolean isQuestRewardAvailable(Quest quest) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false);
        }
        return true;
    }

    public static boolean isQuestStartable(boolean original, Quest quest) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            return original;
        }

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return false;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            DependencyRequirement requirement = quest.dependencyRequirement;
            if (quest.dependencies.isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                // no dependencies, check if it has self
                return false;
            }
            if (requirement.one) {
                if (quest.dependencies.stream()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need one dependency, check if it has any
                    return false;
                }
            } else {
                if (!quest.dependencies.stream()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency)
                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need all dependency, check if it has all
                    return false;
                }
            }
        }
        else {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                return false;
            }
        }
        return original;
    }
}
