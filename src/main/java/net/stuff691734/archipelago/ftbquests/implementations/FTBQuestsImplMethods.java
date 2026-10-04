package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelagoLib.interfaces.FTBQuestsInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FTBQuestsImplMethods {
    public static Optional<FTBQuestsInterface> getFTBQuest(String questName) {
        int id;
        try {
            id = Integer.parseUnsignedInt(questName, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questName);
            return Optional.empty();
        }
        QuestObject questObject = ServerQuestFile.INSTANCE.get(id);
        if (questObject instanceof Quest) {
            return Optional.of(new FTBQuestsImpl((Quest) questObject));
        }
        return Optional.empty();
    }

    public static List<FTBQuestsInterface> getAllFTBQuests() {
        List<FTBQuestsInterface> list = new ArrayList<>();
        for (Chapter chapter : ServerQuestFile.INSTANCE.chapters) {
            for (Quest quest : chapter.quests) {
                list.add(new FTBQuestsImpl(quest));
            }
        }
        return list;
    }

    public static boolean isQuestId(String questId) {
        int id;
        try {
            id = Integer.parseUnsignedInt(questId, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
        return ServerQuestFile.INSTANCE.get(id) != null;
    }
}
