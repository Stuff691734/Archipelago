package net.stuff691734.archipelago.ftbquests.implementations;

import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
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
        for (ChapterGroup chapterGroup : ServerQuestFile.INSTANCE.chapterGroups) {
            for (Chapter chapter : chapterGroup.chapters) {
                for (Quest quest : chapter.getQuests()) {
                    list.add(new FTBQuestsImpl(quest));
                }
            }
        }
        return list;
    }
}
