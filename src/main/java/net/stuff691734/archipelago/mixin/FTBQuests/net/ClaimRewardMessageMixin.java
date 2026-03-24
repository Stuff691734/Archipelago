package net.stuff691734.archipelago.mixin.FTBQuests.net;

import dev.ftb.mods.ftbquests.net.ClaimRewardMessage;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClaimRewardMessage.class)
public class ClaimRewardMessageMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;isCompleted(Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"))
    private boolean modifyRewardAccess(TeamData teamData, QuestObject questObject) {
        return FTBUtils.hasQuestRewardAccess(teamData, questObject);
    }
}
