package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.quests.CollectRewardsButton;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(CollectRewardsButton.class)
public class CollectRewardsButtonMixin {
    @Redirect(method = {"anyUnclaimedRewards", "onClicked", "draw"}, at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;hasUnclaimedRewards(Ljava/util/UUID;Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"))
    private boolean archipelago$HasUnclaimedRewards(TeamData teamData, UUID player, QuestObject object) {
        // always called with arguments of this.questScreen.file
        AtomicBoolean hasAvailableReward = new AtomicBoolean(false);
        ((ClientQuestFile) object).forAllQuests(quest -> {
            if (Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl(quest), teamData.isCompleted(quest)) &&
                    quest.getRewards().stream().anyMatch(
                            (reward) -> !teamData.isRewardClaimed(player, reward)
                    )
            ) {
                hasAvailableReward.set(true);
            }
        });
        return hasAvailableReward.get();
    }
}
