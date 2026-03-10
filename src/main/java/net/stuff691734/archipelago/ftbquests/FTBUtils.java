package net.stuff691734.archipelago.ftbquests;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import net.stuff691734.archipelago.Archipelago;

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

}
