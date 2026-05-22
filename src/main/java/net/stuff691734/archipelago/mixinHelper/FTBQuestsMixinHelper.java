package net.stuff691734.archipelago.mixinHelper;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

import java.util.Objects;
import java.util.UUID;

// methods used by Mixin to make code more consistent between coremods and mixins
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, TeamData data, UUID uuid) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape())) {
            if (
                ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString())) &&
                quest.rewards.stream().anyMatch(
                    reward -> !data.isRewardClaimed(uuid, reward)
                )
            ) {
                // got this check but haven't claimed yet
                return ThemeProperties.ALERT_ICON.get(quest);
            }
        }
        return originalIcon != ThemeProperties.ALERT_ICON.get(quest) ? originalIcon : Color4I.EMPTY;
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

        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getChapter().getCodeString()))) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                return false;
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            if (quest.hasDependencies()) {
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
                    if (quest.getDependencies().filter(FTBUtils::hasRequiredChecks).count() < quest.minRequiredDependencies) {
                        // checks if it has less than the minimum required
                        return false;
                    }
                }
                else if (quest.dependencyRequirement.one) {
                    if (quest.getDependencies().noneMatch(FTBUtils::hasRequiredChecks)) {
                        // need one dependency, check if it has any
                        return false;
                    }
                } else {
                    if (!quest.getDependencies().allMatch(FTBUtils::hasRequiredChecks)) {
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
}