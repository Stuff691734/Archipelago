package net.stuff691734.archipelago.ftbquests;

import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.archipelagoData.CheckType;

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
        return ServerQuestFile.INSTANCE.get(id) != null;
    }

    public static boolean hasQuestRewardAccess(Quest quest, Function<Quest, Boolean> action) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
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

    public static boolean hasRequiredChecks(QuestObject questObject) {
        if (questObject instanceof Task) {
            // operate on quest that the task belongs to
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(((Task) questObject).quest.getCodeString()));
        }

        if (questObject instanceof Chapter) {
            // if chapter check that we have all quests from chapter
            return ((Chapter) questObject).quests.stream().allMatch(FTBUtils::hasRequiredChecks);
        }

        if (questObject instanceof Quest) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(questObject.getCodeString()));
        }
        return false;
    }
}
