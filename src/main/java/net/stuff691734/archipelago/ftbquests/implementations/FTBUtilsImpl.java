package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.UtilsImpl;

public class FTBUtilsImpl extends UtilsImpl {
    @Override
    public boolean isQuestId(String questId) {
        long id;
        try {
            id = Long.parseLong(questId, 16);
        } catch (NumberFormatException exception) {
            Archipelago.LOGGER.error("Unable to parse quest: {}", questId);
            return false;
        }
        return ServerQuestFile.INSTANCE.get(id) != null;
    }
}
