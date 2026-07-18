package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.DependencyRequirement;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.reward.RewardClaimType;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerData.class)
public class PlayerDataMixin {
    @Inject(
            method = "areDependenciesComplete",
            at = @At(value = "RETURN"),
            cancellable = true,
            remap = false
    )
    private void preventTaskCompletion(Quest quest, CallbackInfoReturnable<Boolean> cir) {
        if (!Archipelago.logic.isFTBQuestCompletable(new FTBQuestsImpl(quest), cir.getReturnValue())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getClaimType", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void preventRewardAccess(Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl(reward.quest), !cir.getReturnValue().isClaimed())) {
            cir.setReturnValue(RewardClaimType.CAN_CLAIM);
        } else {
            cir.setReturnValue(RewardClaimType.CANT_CLAIM);
        }
    }
}
