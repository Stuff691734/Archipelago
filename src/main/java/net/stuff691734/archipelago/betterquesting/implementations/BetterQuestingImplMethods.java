package net.stuff691734.archipelago.betterquesting.implementations;

import betterquesting.api.questing.IQuest;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelagoLib.interfaces.BetterQuestingInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BetterQuestingImplMethods {
    public static Optional<BetterQuestingInterface> getBetterQuestingQuest(String name) {
        int id;
        try {
            id = Integer.parseUnsignedInt(name);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", name);
            return Optional.empty();
        }
        IQuest questObject = QuestDatabase.INSTANCE.getValue(id);
        if (questObject != null) {
            return Optional.of(new BetterQuestingQuestImpl(questObject));
        }
        return Optional.empty();
    }

    public static List<BetterQuestingInterface> getAllBetterQuestingQuests() {
        List<BetterQuestingInterface> list = new ArrayList<>();
        QuestDatabase.INSTANCE.getEntries().forEach(
                (quest) -> list.add(new BetterQuestingQuestImpl(quest.getValue()))
        );
        return list;
    }

    public static boolean isBetterQuestingQuestId(String questId) {
        if (questId.startsWith("c-")) {
            int id;
            try {
                id = Integer.parseUnsignedInt(questId.substring(2));
                return QuestLineDatabase.INSTANCE.getValue(id) != null;
            } catch (NumberFormatException exception) {
                Archipelago.LOGGER.error("Unable to parse quest chapter: {}", questId);
                return false;
            }
        }
        int id;
        try {
            id = Integer.parseUnsignedInt(questId);
            return QuestDatabase.INSTANCE.getValue(id) != null;
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
    }
}
