package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.CollectRewardsButton;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.archipelagoData.CheckType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.quest_checks_give_rewards ||
                !Archipelago.slotData.activated_modules.contains("FTBQuests")
            )
        ) {
            return teamData.hasUnclaimedRewards(player, object);
        }
        // always called with arguments of this.questScreen.file
        ClientQuestFile questFile = (ClientQuestFile) object;
        AtomicBoolean hasAvailableReward = new AtomicBoolean(false);
        questFile.forAllQuests(quest -> {
            if (
                ArchipelagoPersistentState.getCheck(CheckType.FTB_QUEST.addPrefix(quest.getCodeString())) &&
                quest.getRewards().stream().anyMatch(reward -> !teamData.isRewardClaimed(player, reward))
            ) {
                hasAvailableReward.set(true);
            }
        });
        return hasAvailableReward.get();
    }
}
