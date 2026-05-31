package net.stuff691734.archipelago.mixinHelper;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.task.AdvancementTask;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.task.TaskTypes;
import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import com.feed_the_beast.mods.ftbguilibrary.widget.ContextMenuItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// methods used by Mixin to make code more consistent between coremods and mixins
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, PlayerData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            if (
                ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString())) &&
                quest.rewards.stream().anyMatch(
                    reward -> !data.isRewardClaimed(reward.id)
                )
            ) {
                // got this check but haven't claimed yet
                return ThemeProperties.ALERT_ICON.get(quest);
            }
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : Icon.EMPTY;
    }

    public static boolean isQuestRewardAvailable(Quest quest, PlayerData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
        }
        return data.isComplete(quest);
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

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getChapter().getCodeString()))) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return false;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (quest.dependencies.isEmpty()) {
                if (!Archipelago.slotData.roots_unlocked) {
                    if (!FTBUtils.hasRequiredChecks(quest)) {
                        return false;
                    }
                    if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()))) {
                        // no dependencies, check if it has self
                        return false;
                    }
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
            if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()))) {
                return false;
            }
        }
        return original;
    }

    @OnlyIn(Dist.CLIENT)
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
            addToMenu(contextMenu, "Archipelago Item: ftb " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getChapter().getCodeString(), quest.getChapter().getTitle())), 0);
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            List<String> advancementDependencies = quest.tasks.stream()
                    .filter((task) -> task.getType() == TaskTypes.ADVANCEMENT).distinct()
                    .map((task) -> ((AdvancementTask)task).advancement).collect(Collectors.toList());

            int indent = 0;

            if (!advancementDependencies.isEmpty()) {
                addToMenu(contextMenu, "All of: {", indent++);
                for (String advancement : advancementDependencies) {
                    if (Minecraft.getInstance().player != null) {
                        Advancement advancementDetails = Minecraft.getInstance().player.connection.getAdvancements().getAdvancements().get(new ResourceLocation(advancement));
                        if (advancementDetails != null && advancementDetails.getDisplay() != null) {
                            addToMenu(contextMenu, CheckType.ADVANCEMENT.addPrefix(String.format("%s (%s)", advancement, advancementDetails.getDisplay().getTitle().getString())), indent);
                        }
                    }
                }
            }
            if (quest.dependencies.isEmpty()) {
                if (!Archipelago.slotData.roots_unlocked) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle().getString())), indent);
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
                        addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest1.getCodeString(), quest1.getTitle().getString())), indent);
                    }
                    addToMenu(contextMenu, "}", --indent);
                } else if (dependency instanceof Task) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", ((Task)dependency).quest.getCodeString(), ((Task)dependency).quest.getTitle().getString())), indent);
                } else {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", dependency.getCodeString(), dependency.getTitle().getString())), indent);
                }
            }
            addToMenu(contextMenu, "}", --indent);
            if (!advancementDependencies.isEmpty() && (quest.minRequiredDependencies != 0 || quest.dependencyRequirement.one)) {
                addToMenu(contextMenu, "}", --indent);
            }
        }
        else {
            addToMenu(contextMenu, "Archipelago Item: " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle().getString())), 0);
        }
    }

    private static void addToMenu(List<ContextMenuItem> contextMenu, String text, int indent) {
        String indentText = indent >= 0 ? new String(new char[indent]).replace("\0", "  ") : "";
        contextMenu.add(new ContextMenuItem(new StringTextComponent(indentText +  text), Color4I.EMPTY, null));
    }
}