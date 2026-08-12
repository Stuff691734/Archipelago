package net.stuff691734.archipelago.mixin;

import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftbquests.client.ClientQuestData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import net.stuff691734.archipelagoLib.CheckType;

import java.util.Collection;
import java.util.List;

public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, ClientQuestData data) {
        if (Archipelago.logic.isFTBQuestRewardObtained(
                new FTBQuestsImpl(quest),
                quest.rewards.stream().anyMatch(
                        (reward) -> !data.isRewardClaimedSelf(reward)
                )
        )) {
            return ThemeProperties.ALERT_ICON.get(quest);
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : Icon.EMPTY;
    }

    public static boolean isQuestRewardAvailable(Quest quest, QuestData data) {
        return Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl(quest), quest.isComplete(data));
    }

    public static void sendArchipelagoQuest(Quest quest) {
        Archipelago.client.sendCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
    }

    public static boolean isQuestStartable(boolean original, Quest quest) {
        if (!Archipelago.logic.isFTBQuestCompletable(new FTBQuestsImpl(quest), original)) {
            return false;
        }
        return original;
    }

    public static boolean alwaysHaveDependencies(Quest quest) {
        return Archipelago.logic.isFTBQuestRandomized(new FTBQuestsImpl(quest));
    }

    public static void addArchipelagoDependency(Quest quest, List<ContextMenuItem> contextMenu, Collection<QuestObject> dependencyList) {
        if (quest != null && quest.dependencies == dependencyList) {
            List<String> items = Archipelago.logic.addDependencies(
                    (advancement) -> new AdvancementImpl(Minecraft.getMinecraft().player.connection.getAdvancementManager().getAdvancementList().getAdvancement(new ResourceLocation(advancement))),
                    new FTBQuestsImpl(quest)
            );
            for (String item : items) {
                contextMenu.add(new ContextMenuItem(item, Color4I.EMPTY, null));
            }
        }
    }
}
