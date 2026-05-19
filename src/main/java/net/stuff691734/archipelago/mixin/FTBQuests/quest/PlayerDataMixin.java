package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.*;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.reward.RewardClaimType;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerData.class)
public abstract class PlayerDataMixin {
    @Shadow(remap = false)
    @Final
    public QuestFile file;

    @Shadow(remap = false)
    public abstract boolean isRewardClaimed(int id);

    @Inject(
            method = "areDependenciesComplete",
            at = @At(value = "RETURN"),
            cancellable = true,
            remap = false
    )
    private void preventTaskCompletion(Quest quest, CallbackInfoReturnable<Boolean> cir) {
        if (!FTBQuestsMixinHelper.isQuestStartable(cir.getReturnValue(), quest)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getClaimType", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void preventRewardAccess(Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (!cir.getReturnValue().isClaimed() && Archipelago.slotData.isFTBQuestRewardRandomized(reward.quest.getShape())) {
            if (ArchipelagoPersistentState.getCheck(reward.quest.getCodeString())) {
                cir.setReturnValue(RewardClaimType.CAN_CLAIM);
            } else {
                cir.setReturnValue(RewardClaimType.CANT_CLAIM);
            }
        }
    }
    
    @Inject(method = "hasUnclaimedRewards()Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void hasUnclaimedRewards(CallbackInfoReturnable<Boolean> cir) {
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
        for(Chapter chapter : this.file.chapters) {
            for(Quest quest : chapter.quests) {
                if (
                    FTBUtils.hasQuestRewardAccess(quest) &&
                    quest.rewards.stream().anyMatch(reward -> !this.isRewardClaimed(reward.id))
                ) {
                    hasAvailableReward = true;
                }
            }
        }
        cir.setReturnValue(hasAvailableReward);
    }
}
