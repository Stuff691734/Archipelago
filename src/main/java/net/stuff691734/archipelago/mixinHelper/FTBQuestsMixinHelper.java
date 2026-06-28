package net.stuff691734.archipelago.mixinHelper;

import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import net.stuff691734.archipelago.ftbquests.FTBUtils;

import java.util.Objects;

// methods used by Mixin to make code more consistent between coremods and mixins
public class FTBQuestsMixinHelper {
    public static Icon getQuestIcon(Quest quest, Icon originalIcon, PlayerData data) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape().id)) {
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
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape().id)) {
            return ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
        }
        return data.isComplete(quest);
    }

    public static void sendArchipelagoQuest(Quest quest) {
        Utils.sendCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString()));
    }

    public static boolean isQuestRewardAvailable(Quest quest) {
        if (Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape().id)) {
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
}