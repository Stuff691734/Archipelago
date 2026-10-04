package net.stuff691734.archipelago.mixin;

import betterquesting.api.enums.EnumLogic;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.client.gui2.editors.GuiQuestEditor;
import betterquesting.questing.QuestInstance;
import net.minecraft.util.text.TextFormatting;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.betterquesting.implementations.BetterQuestingQuestImpl;

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

    // TODO: add needed archipelago checks to the hover display
//     PanelButtonQuest.getStandardTooltip()

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

}
