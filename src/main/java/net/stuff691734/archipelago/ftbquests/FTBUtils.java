package net.stuff691734.archipelago.ftbquests;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.stuff691734.archipelago.Archipelago;

import java.util.function.Function;

public class FTBUtils {
    public static boolean isQuestId(String questId) {
        long id;
        try {
            id = Long.parseLong(questId, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
        return ClientQuestFile.INSTANCE.getQuest(id) != null;
    }

    public static boolean hasQuestRewardAccess(Quest quest, Function<Quest, Boolean> action) {
        if (isQuestRewardRandomized(quest)) {
            return Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false);
        }
        return action.apply(quest);
    }

    public static boolean hasQuestRewardAccess(Quest quest) {
        return hasQuestRewardAccess(quest, (Quest q) -> false);
    }

    public static boolean hasQuestRewardAccess(QuestObject questObject, Function<QuestObject, Boolean> action) {
        if (questObject instanceof Quest quest) {
            return hasQuestRewardAccess(quest, (Function<Quest, Boolean>) action::apply);
        }
        return action.apply(questObject);
    }

    public static boolean isQuestRewardRandomized(Quest quest) {
        return !Archipelago.slotData.isInitiated ||
            (
                Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                Archipelago.slotData.ftb_quest_shape.contains(quest.getShape()) &&
                Archipelago.slotData.quest_checks_give_rewards
            );
    }

}
