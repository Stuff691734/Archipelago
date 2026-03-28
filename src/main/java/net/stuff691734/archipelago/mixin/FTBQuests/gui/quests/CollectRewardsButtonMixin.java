package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.gui.quests.CollectRewardsButton;
import dev.ftb.mods.ftbquests.quest.*;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(CollectRewardsButton.class)
public class CollectRewardsButtonMixin {

    @Redirect(method = "onClicked", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;hasUnclaimedRewards(Ljava/util/UUID;Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"), remap = false)
    public boolean onClickedHasUnclaimedRewards(TeamData teamData, UUID player, QuestObject object) {
        return archipelago$HasUnclaimedRewards(teamData, player, object);
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;hasUnclaimedRewards(Ljava/util/UUID;Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"), remap = false)
    public boolean drawHasUnclaimedRewards(TeamData teamData, UUID player, QuestObject object) {
        return archipelago$HasUnclaimedRewards(teamData, player, object);
    }

    @Unique
    private boolean archipelago$HasUnclaimedRewards(TeamData teamData, UUID player, QuestObject object) {
        if (
            !Archipelago.slotData.isInitiated ||
            (
                !Archipelago.slotData.quest_checks_give_rewards ||
                !Archipelago.slotData.activated_modules.contains("FTBQuests")
            )
        ) {
            return teamData.hasUnclaimedRewards(player, object);
        }
        // always called with arguments of this.questScreen.file
        ClientQuestFile questFile = (ClientQuestFile) object;
        boolean hasAvailableReward = false;
        for(ChapterGroup group : questFile.chapterGroups) {
            for(Chapter chapter : group.chapters) {
                for(Quest quest : chapter.quests) {
                    if (
                        FTBUtils.hasQuestRewardAccess(quest) &&
                        quest.rewards.stream().anyMatch(reward -> !teamData.isRewardClaimed(player, reward))
                    ) {
                        hasAvailableReward = true;
                    }
                }
            }
        }
        return hasAvailableReward;
    }
}
