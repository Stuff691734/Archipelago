package net.stuff691734.archipelago.ftbquests;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.server.level.ServerPlayer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;

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
        return FTBQuestsAPI.api().getQuestFile(true).getQuest(id) != null;
    }

    public static boolean hasQuestRewardAccess(Quest quest, Function<Quest, Boolean> action) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
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

    public static void checkIsCompleted(ServerPlayer player, String questName) {
        // not doing this safely as it has already been checked that this works via FTBUtils.isQuestId()
        long questId = Long.parseLong(questName, 16);
        ClientQuestFile file = ClientQuestFile.INSTANCE;
        QuestObject questObject = file.get(questId);
        if (questObject instanceof Quest quest) {
            ((QuestAccessor)(Object)quest).archipelago$checkForDependantCompletion(TeamData.get(player));
        }
    }
}
