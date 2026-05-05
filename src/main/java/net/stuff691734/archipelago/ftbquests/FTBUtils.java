package net.stuff691734.archipelago.ftbquests;

import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;

import java.util.function.Function;

public class FTBUtils {
    public static boolean isQuestId(String questId) {
        int id;
        try {
            id = Integer.parseUnsignedInt(questId, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
        return ServerQuestFile.INSTANCE.getQuest(id) != null;
    }

    public static boolean hasQuestRewardAccess(Quest quest, Function<Quest, Boolean> action) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getFtbQuest(quest.getCodeString());
        }
        return action.apply(quest);
    }

    public static boolean hasQuestRewardAccess(Quest quest) {
        return hasQuestRewardAccess(quest, (Quest q) -> false);
    }

    public static boolean hasQuestRewardAccess(QuestObject questObject, Function<QuestObject, Boolean> action) {
        if (questObject instanceof Quest) {
            Quest quest = (Quest)questObject;
            return hasQuestRewardAccess(quest, (Function<Quest, Boolean>) action::apply);
        }
        return action.apply(questObject);
    }
}
