package net.stuff691734.archipelago.mixin;

import com.feed_the_beast.ftblib.lib.gui.ContextMenuItem;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftbquests.client.ClientQuestData;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.task.AdvancementTask;
import com.feed_the_beast.ftbquests.quest.task.FTBQuestsTasks;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// used by class transformers, even though it appears to be unused
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, ClientQuestData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            if (
                    ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString())) &&
                    quest.rewards.stream().anyMatch(
                        reward -> !data.isRewardClaimedSelf(reward)
                    )
            ) {
                // got this check but haven't claimed yet
                return ThemeProperties.ALERT_ICON.get(quest);
            }
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : Icon.EMPTY;
    }

    public static boolean isQuestRewardAvailable(Quest quest, QuestData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
        }
        return quest.isComplete(data);
    }

    public static void sendArchipelagoQuest(Quest quest) {
        Utils.sendCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
    }

    public static boolean isQuestRewardAvailable(Quest quest) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
        }
        return true;
    }

    public static boolean isQuestStartable(boolean original, Quest quest) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            return original;
        }
        if (Archipelago.slotData.roots_unlocked && quest.dependencies.isEmpty()) {
            return true;
        }

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getChapter().getCodeString()))) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return original;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (quest.dependencies.isEmpty()) {
                if (!FTBUtils.hasRequiredChecks(quest)) {
                    return false;
                }
            }
            else {
                if (quest.minRequiredDependencies != 0) {
                    if (quest.dependencies.stream().filter(FTBUtils::hasRequiredChecks).count() < quest.minRequiredDependencies) {
                        // checks if it has less than the minimum required
                        return false;
                    }
                }
                else if (quest.dependencyRequirement.one) {
                    if (quest.dependencies.stream().noneMatch(FTBUtils::hasRequiredChecks)) {
                        // need one dependency, check if it has any
                        return false;
                    }
                } else {
                    if (!quest.dependencies.stream().allMatch(FTBUtils::hasRequiredChecks)) {
                        // need all dependencies, check if it has all
                        return false;
                    }
                }
            }
        }
        else {
            if (!FTBUtils.hasRequiredChecks(quest)) {
                return false;
            }
        }
        return original;
    }

    public static void addDependencies(List<ContextMenuItem> contextMenu, Quest quest) {
        if (Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            return;
        }

        // only do this for dependencies
        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!(quest.dependencies.isEmpty() && Archipelago.slotData.roots_unlocked)) {
                addToMenu(contextMenu, "Archipelago Item: ftb " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getChapter().getCodeString(), quest.getChapter().getTitle())), 0);
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            List<String> advancementDependencies = quest.tasks.stream()
                    .filter((task) -> task.getType() == FTBQuestsTasks.ADVANCEMENT).distinct()
                    .map((task) -> ((AdvancementTask)task).advancement).collect(Collectors.toList());

            int indent = 0;

            if (!advancementDependencies.isEmpty()) {
                addToMenu(contextMenu, "All of: {", indent++);
                for (String advancement : advancementDependencies) {
                    if (Minecraft.getMinecraft().player != null) {
                        Advancement advancementDetails = Minecraft.getMinecraft().player.connection.getAdvancementManager().getAdvancementList().getAdvancement(new ResourceLocation(advancement));
                        if (advancementDetails != null && advancementDetails.getDisplay() != null) {
                            addToMenu(contextMenu, CheckType.ADVANCEMENT.addPrefix(String.format("%s (%s)", advancement, advancementDetails.getDisplay().getTitle())), indent);
                        }
                    }
                }
            }
            if (quest.dependencies.isEmpty()) {
                if (!Archipelago.slotData.roots_unlocked) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle())), indent);
                } else {
                    if (!advancementDependencies.isEmpty()) {
                        addToMenu(contextMenu, "}", --indent);
                    }
                    return;
                }
            } else if (quest.minRequiredDependencies != 0) {
                addToMenu(contextMenu, "At Least " + quest.minRequiredDependencies + " of: {", indent++);
            } else if (quest.dependencyRequirement.one) {
                addToMenu(contextMenu, "One of: {", indent++);
            } else {
                if (advancementDependencies.isEmpty()) {
                    addToMenu(contextMenu, "All of: {", indent++);
                }
            }
            for (QuestObject dependency : quest.dependencies) {
                if (dependency instanceof Chapter) {
                    addToMenu(contextMenu, "All of: {", indent++);
                    for (Quest quest1 : ((Chapter) dependency).quests) {
                        addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest1.getCodeString(), quest1.getTitle())), indent);
                    }
                    addToMenu(contextMenu, "}", --indent);
                } else if (dependency instanceof Task) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", ((Task)dependency).quest.getCodeString(), ((Task)dependency).quest.getTitle())), indent);
                } else {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", dependency.getCodeString(), dependency.getTitle())), indent);
                }
            }
            addToMenu(contextMenu, "}", --indent);
            if (!advancementDependencies.isEmpty() && (quest.minRequiredDependencies != 0 || quest.dependencyRequirement.one)) {
                addToMenu(contextMenu, "}", --indent);
            }
        }
        else {
            addToMenu(contextMenu, "Archipelago Item: " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle())), 0);
        }
    }

    private static void addToMenu(List<ContextMenuItem> contextMenu, String text, int indent) {
        String indentText = indent >= 0 ? new String(new char[indent]).replace("\0", "  ") : "";
        contextMenu.add(new ContextMenuItem(indentText +  text, Color4I.EMPTY, null));
    }

    public static boolean alwaysHaveDependencies(Quest quest) {
        if (!Archipelago.slotData.isInitiated) {
            return false;
        }
        if (
                Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                        Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
        ) {
            if (Archipelago.slotData.roots_unlocked) {
                return quest.dependencies.isEmpty();
            }
            return false;
        }
        return quest.dependencies.isEmpty();
    }

    public static void addArchipelagoDependency(Quest quest, List<ContextMenuItem> contextMenu, Collection<QuestObject> dependencyList) {
        if (quest != null && quest.dependencies == dependencyList) {
            addDependencies(contextMenu, quest);
        }
    }
}
