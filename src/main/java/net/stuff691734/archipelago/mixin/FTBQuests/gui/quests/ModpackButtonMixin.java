package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.gui.quests.ModpackButton;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.ChapterGroup;
import com.feed_the_beast.ftbquests.quest.Quest;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModpackButton.class)
public abstract class ModpackButtonMixin {
    @Inject(method = "hasUnclaimedRewards", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void onClickedHasUnclaimedRewards(ClientQuestFile questFile, CallbackInfoReturnable<Boolean> cir) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.quest_checks_give_rewards ||
                !Archipelago.slotData.activated_modules.contains("FTBQuests")
            )
        ) {
            return;
        }
        boolean hasAvailableReward = false;
        for(ChapterGroup group : questFile.chapterGroups) {
            for(Chapter chapter : group.chapters) {
                for(Quest quest : chapter.quests) {
                    if (
                        FTBUtils.hasQuestRewardAccess(quest) &&
                        quest.rewards.stream().anyMatch(reward -> !questFile.self.isRewardClaimed(reward.id))
                    ) {
                        hasAvailableReward = true;
                    }
                }
            }
        }
        cir.setReturnValue(hasAvailableReward);
    }
}
