package net.stuff691734.archipelago.mixinHelper;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import net.stuff691734.archipelago.mixin.FTBQuests.quest.task.AdvancementTaskAccessor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

// methods used by Mixin to make code more consistent between coremods and mixins
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, TeamData data, UUID uuid) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            if (
                ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString())) &&
                quest.getRewards().stream().anyMatch(
                    reward -> !data.isRewardClaimed(uuid, reward)
                )
            ) {
                // got this check but haven't claimed yet
                return ThemeProperties.ALERT_ICON.get(quest);
            }
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : ThemeProperties.QUEST_NOT_STARTED_COLOR.get(quest);
    }

    public static boolean isQuestRewardAvailable(Quest quest, TeamData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
        }
        return data.isCompleted(quest);
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
        if (Archipelago.slotData.roots_unlocked && !quest.hasDependencies()) {
            return true;
        }

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getChapter().getCodeString()))) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return original;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (quest.hasDependencies()) {
                if (!FTBUtils.hasRequiredChecks(quest)) {
                    return false;
                }
            }
            else {
                if (quest.getMinRequiredDependencies() != 0) {
                    if (quest.streamDependencies().filter((q) -> FTBUtils.hasRequiredChecks(q)).count() < quest.getMinRequiredDependencies()) {
                        // checks if it has less than the minimum required
                        return false;
                    }
                }
                else if (((QuestAccessor)(Object) quest).archipelago$getDependencyRequirement().needOnlyOne()) {
                    if (quest.streamDependencies().noneMatch((q) -> FTBUtils.hasRequiredChecks(q))) {
                        // need one dependency, check if it has any
                        return false;
                    }
                } else {
                    // using a method reference here results in a IllegalAccessException
                    if (!quest.streamDependencies().allMatch((q) -> FTBUtils.hasRequiredChecks(q))) {
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
            if (!(quest.streamDependencies().toList().isEmpty() && Archipelago.slotData.roots_unlocked)) {
                addToMenu(contextMenu, "Archipelago Item: ftb " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getChapter().getCodeString(), quest.getChapter().getTitle())), 0);
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            List<String> advancementDependencies = quest.getTasks().stream()
                    .filter((task) -> task.getType() == TaskTypes.ADVANCEMENT).distinct()
                    .map((task) -> ((AdvancementTaskAccessor)task).archipelago$advancement().toString()).toList();

            int indent = 0;

            if (!advancementDependencies.isEmpty()) {
                addToMenu(contextMenu, "All of: {", indent++);
                for (String advancement : advancementDependencies) {
                    if (Minecraft.getInstance().player != null) {
                        AdvancementNode advancementDetails = Minecraft.getInstance().player.connection.getAdvancements().getTree().get(ResourceLocation.parse(advancement));
                        if (advancementDetails != null && advancementDetails.advancement().display().isPresent()) {
                            addToMenu(contextMenu, CheckType.ADVANCEMENT.addPrefix(String.format("%s (%s)", advancement, advancementDetails.advancement().display().get().getTitle().getString())), indent);
                        }
                    }
                }
            }
            if (quest.streamDependencies().toList().isEmpty()) {
                if (!Archipelago.slotData.roots_unlocked) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle().getString())), indent);
                } else {
                    if (!advancementDependencies.isEmpty()) {
                        addToMenu(contextMenu, "}", --indent);
                    }
                    return;
                }
            } else if (quest.getMinRequiredDependencies() != 0) {
                addToMenu(contextMenu, "At Least " + quest.getMinRequiredDependencies() + " of: {", indent++);
            } else if (((QuestAccessor)(Object) quest).archipelago$getDependencyRequirement().needOnlyOne()) {
                addToMenu(contextMenu, "One of: {", indent++);
            } else {
                if (advancementDependencies.isEmpty()) {
                    addToMenu(contextMenu, "All of: {", indent++);
                }
            }
            for (QuestObject dependency : quest.streamDependencies().toList()) {
                if (dependency instanceof ChapterGroup) {
                    addToMenu(contextMenu, "All of: {", indent++);
                    for (Chapter chapter : ((ChapterGroup) dependency).getChapters()) {
                        for (Quest quest1 : chapter.getQuests()) {
                            addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest1.getCodeString(), quest1.getTitle().getString())), indent);
                        }
                    }
                    addToMenu(contextMenu, "}", --indent);
                }
                if (dependency instanceof Chapter) {
                    addToMenu(contextMenu, "All of: {", indent++);
                    for (Quest quest1 : ((Chapter) dependency).getQuests()) {
                        addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest1.getCodeString(), quest1.getTitle().getString())), indent);
                    }
                    addToMenu(contextMenu, "}", --indent);
                } else if (dependency instanceof Task) {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", ((Task)dependency).getQuest().getCodeString(), ((Task)dependency).getQuest().getTitle().getString())), indent);
                } else {
                    addToMenu(contextMenu, CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", dependency.getCodeString(), dependency.getTitle().getString())), indent);
                }
            }
            addToMenu(contextMenu, "}", --indent);
            if (!advancementDependencies.isEmpty() && (quest.getMinRequiredDependencies() != 0 || ((QuestAccessor)(Object) quest).archipelago$getDependencyRequirement().needOnlyOne())) {
                addToMenu(contextMenu, "}", --indent);
            }
        }
        else {
            addToMenu(contextMenu, "Archipelago Item: " + CheckType.FTB_QUEST.addPrefix(String.format("%s (%s)", quest.getCodeString(), quest.getTitle().getString())), 0);
        }
    }

    private static void addToMenu(List<ContextMenuItem> contextMenu, String text, int indent) {
        contextMenu.add(new ContextMenuItem(Component.literal("  ".repeat(indent) + text), Color4I.empty(), null));
    }
}