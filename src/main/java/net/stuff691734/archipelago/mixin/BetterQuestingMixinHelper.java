package net.stuff691734.archipelago.mixin;

import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.questing.IQuest;
import betterquesting.questing.QuestDatabase;
import net.minecraft.util.text.TextFormatting;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.betterquesting.implementations.BetterQuestingQuestImpl;
import net.stuff691734.archipelagoLib.CheckType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;

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

    public static boolean isUnlocked(IQuest quest, BooleanSupplier original) {
        return Archipelago.logic.isBetterQuestingQuestStartable(new BetterQuestingQuestImpl(quest), original.getAsBoolean());

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

    public static void sendCheck(IQuest quest) {
        Archipelago.client.sendCheck(CheckType.BETTER_QUESTING.addPrefix(
                String.valueOf(QuestDatabase.INSTANCE.getID(quest))
        ));
    }

    public static EnumQuestState getState(IQuest quest, UUID uuid) {
        // unclaimed will now have priority instead of completed.
        if (!quest.hasClaimed(uuid) && Archipelago.logic.isBetterQuestingQuestRewardObtained(new BetterQuestingQuestImpl(quest), quest.isComplete(uuid) && !quest.hasClaimed(uuid))) {
            return EnumQuestState.UNCLAIMED;
        }
        if (quest.isComplete(uuid)) {
            return EnumQuestState.COMPLETED;
        }
        if (quest.isUnlocked(uuid)) {
            return EnumQuestState.UNLOCKED;
        } else {
            return EnumQuestState.LOCKED;
        }
    }

    public static boolean canClaim(IQuest quest, BooleanSupplier original, UUID uuid) {
        return Archipelago.logic.isBetterQuestingQuestRewardObtained(new BetterQuestingQuestImpl(quest), original.getAsBoolean()) && !quest.hasClaimed(uuid);
    }

    public static boolean canSubmit(IQuest quest, BooleanSupplier original, UUID uuid) {
        return Archipelago.logic.isBetterQuestingQuestStartable(new BetterQuestingQuestImpl(quest), original.getAsBoolean()) && quest.isUnlocked(uuid);
    }
}
