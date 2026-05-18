package net.stuff691734.archipelago.mixin.FTBQuests.net;

import com.feed_the_beast.ftbquests.net.MessageClaimReward;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MessageClaimReward.class)
public class MessageClaimRewardMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/PlayerData;isComplete(Lcom/feed_the_beast/ftbquests/quest/QuestObject;)Z"), remap = false)
    private boolean modifyRewardAccess(PlayerData playerData, QuestObject questObject) {
        return FTBQuestsMixinHelper.isQuestRewardAvailable((Quest) questObject, playerData);
    }
}
