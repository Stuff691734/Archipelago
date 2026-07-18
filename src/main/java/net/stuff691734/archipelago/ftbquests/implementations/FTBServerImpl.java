package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.minecraft.server.MinecraftServer;
import net.stuff691734.archipelago.implementations.ServerImpl;
import net.stuff691734.archipelagoLib.interfaces.FTBQuestsInterface;

import java.util.ArrayList;
import java.util.List;

public class FTBServerImpl extends ServerImpl {
    public FTBServerImpl(MinecraftServer server) {
        super(server);
    }

    @Override
    public List<FTBQuestsInterface> getAllFTBQuests() {
        List<FTBQuestsInterface> list = new ArrayList<>();
        for (Chapter chapter : ServerQuestFile.INSTANCE.chapters) {
            for (Quest quest : chapter.quests) {
                list.add(new FTBQuestsImpl(quest));
            }
        }
        return list;
    }
}
