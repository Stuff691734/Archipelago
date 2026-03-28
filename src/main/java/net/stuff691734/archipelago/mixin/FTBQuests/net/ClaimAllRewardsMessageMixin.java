package net.stuff691734.archipelago.mixin.FTBQuests.net;

import dev.ftb.mods.ftbquests.net.ClaimAllRewardsMessage;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClaimAllRewardsMessage.class)
public class ClaimAllRewardsMessageMixin {
    @Redirect(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;isCompleted(Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"), remap = false)
    private static boolean isCompleted(TeamData teamData, QuestObject object) {
        return FTBUtils.hasQuestRewardAccess(object, teamData::isCompleted);
    }
}
