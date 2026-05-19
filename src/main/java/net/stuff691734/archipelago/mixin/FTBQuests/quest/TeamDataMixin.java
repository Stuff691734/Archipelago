package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardClaimType;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(TeamData.class)
public class TeamDataMixin {
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
    private void preventRewardAccess(UUID player, Reward reward, CallbackInfoReturnable<RewardClaimType> cir) {
        if (!cir.getReturnValue().isClaimed() && Archipelago.slotData.isFTBQuestRewardRandomized(reward.getQuest().getShape())) {
            if (ArchipelagoPersistentState.getCheck(reward.getQuest().getCodeString())) {
                cir.setReturnValue(RewardClaimType.CAN_CLAIM);
            } else {
                cir.setReturnValue(RewardClaimType.CANT_CLAIM);
            }
        }
    }
}
