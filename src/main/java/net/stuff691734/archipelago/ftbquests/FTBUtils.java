package net.stuff691734.archipelago.ftbquests;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.Archipelago;

public class FTBUtils {
    public static boolean isQuestId(String questId) {
        long id;
        try {
            id = Long.parseLong(questId, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
        return FTBQuestsAPI.api().getQuestFile(true).getQuest(id) != null;
    }

    public static boolean hasQuestRewardAccess(TeamData teamData, QuestObject questObject) {
        if (questObject instanceof Quest quest) {
            if (
                !Archipelago.slotData.isInitiated ||
                (
                    Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                    Archipelago.slotData.ftb_quest_shape.contains(quest.getShape()) &&
                    Archipelago.slotData.quest_checks_give_rewards
                )
            ) {
                // only modify if it is a quest and it is randomized
                return Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false);
            }
        }
        return teamData.isCompleted(questObject);
    }

}
