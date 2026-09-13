package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.*;
import net.minecraft.server.MinecraftServer;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.ServerImpl;
import net.stuff691734.archipelagoLib.interfaces.FTBQuestsInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FTBServerImpl extends ServerImpl {
    public FTBServerImpl(MinecraftServer server) {
        super(server);
    }

    @Override
    public Optional<FTBQuestsInterface> getFTBQuest(String questName) {
        long id;
        try {
            id = Long.parseLong(questName, 16);
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

    @Override
    public List<FTBQuestsInterface> getAllFTBQuests() {
        List<FTBQuestsInterface> list = new ArrayList<>();
        for (ChapterGroup chapterGroup : ServerQuestFile.INSTANCE.chapterGroups) {
            for (Chapter chapter : chapterGroup.chapters) {
                for (Quest quest : chapter.quests) {
                    list.add(new FTBQuestsImpl(quest));
                }
            }
        }
        return list;
    }
}
