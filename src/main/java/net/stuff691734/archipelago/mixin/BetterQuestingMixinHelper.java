package net.stuff691734.archipelago.mixin;

import betterquesting.api.enums.EnumLogic;
import betterquesting.api.questing.IQuest;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestInstance;
import net.minecraft.util.text.TextFormatting;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.betterquesting.implementations.BetterQuestingQuestImpl;
import net.stuff691734.archipelagoLib.CheckType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BetterQuestingMixinHelper {
    public static boolean getResult(EnumLogic logic, int inputs, int total) {
        switch (logic) {
            case AND:
                return inputs >= total;
            case OR:
            case XOR:
                return inputs > 0;
            default:
                return true;
        }
    }

    public static boolean isUnlocked(QuestInstance quest, boolean original) {
        return Archipelago.logic.isBetterQuestingQuestStartable(new BetterQuestingQuestImpl(quest), original);

    }

    public static void addToTooltip(List<String> tooltip, IQuest quest, UUID playerID) {
        if (!quest.isUnlocked(playerID)) {
            ArrayList<String> list = new ArrayList<>();
            Archipelago.logic.addBetterQuestingDependencyTooltip(list, new BetterQuestingQuestImpl(quest));
            tooltip.add("");
            for (String item : list) {
                tooltip.add(TextFormatting.RED + item);
            }
        }
    }

    public static void sendCheck(QuestInstance quest) {
        Archipelago.client.sendCheck(CheckType.BETTER_QUESTING.addPrefix(
                String.valueOf(QuestDatabase.INSTANCE.getID(quest))
        ));
    }
}
