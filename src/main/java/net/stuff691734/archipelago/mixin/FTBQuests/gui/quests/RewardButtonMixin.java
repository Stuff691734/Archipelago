package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.gui.quests.RewardButton;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RewardButton.class)
public class RewardButtonMixin {
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/PlayerData;isComplete(Lcom/feed_the_beast/ftbquests/quest/QuestObject;)Z"), remap = false)
    private boolean modifyRewardAccess(PlayerData playerData, QuestObject questObject) {
        return FTBQuestsMixinHelper.isQuestRewardAvailable((Quest) questObject, playerData);
    }

    @Redirect(method = "getWidgetType", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/PlayerData;isComplete(Lcom/feed_the_beast/ftbquests/quest/QuestObject;)Z"), remap = false)
    public boolean getWidgetType(PlayerData playerData, QuestObject questObject) {
        // required for allowing user to click on quest reward and get reward
        return FTBQuestsMixinHelper.isQuestRewardAvailable((Quest) questObject, playerData);
    }
}
