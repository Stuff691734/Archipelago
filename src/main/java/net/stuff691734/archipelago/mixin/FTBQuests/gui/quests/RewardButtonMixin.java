package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.gui.quests.ButtonReward;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import net.stuff691734.archipelago.ftbquests.FTBUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ButtonReward.class)
public class RewardButtonMixin {
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/PlayerData;isComplete(Lcom/feed_the_beast/ftbquests/quest/QuestObject;)Z"), remap = false)
    private boolean modifyRewardAccess(PlayerData playerData, QuestObject questObject) {
        return FTBUtils.hasQuestRewardAccess(questObject, playerData::isComplete);
    }

    @Redirect(method = "getWidgetType", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/PlayerData;isComplete(Lcom/feed_the_beast/ftbquests/quest/QuestObject;)Z"), remap = false)
    public boolean getWidgetType(PlayerData playerData, QuestObject questObject) {
        // required for allowing user to click on quest reward and get reward
        return FTBUtils.hasQuestRewardAccess(questObject, playerData::isComplete);
    }
}
