package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.gui.quests.ButtonQuest;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ButtonQuest.class)
public class QuestButtonMixin {
    @Shadow(remap = false)
    public Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/QuestShape;get(Ljava/lang/String;)Lcom/feed_the_beast/ftbquests/quest/QuestShape;"), remap = false, name = "qicon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        return FTBQuestsMixinHelper.getQuestIcon(this.quest, questIcon, PlayerData.get(Minecraft.getInstance().player));
//        if (Archipelago.slotData.isFTBQuestRewardRandomized(this.quest.getShape())) {
//            // only modify if it is a quest and it is randomized and we randomized rewards
//            assert Minecraft.getInstance().player != null; // would've already errored in QuestButton.draw
//            if (
//                Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(this.quest.getCodeString(), false) &&
//                // sadly this undoes some of the optimization done by the ftb team, but teamData.hasUnclaimedRewards returns false when not having access
//                this.quest.rewards.stream().anyMatch(
//                    reward -> !PlayerData.get(Minecraft.getInstance().player).isRewardClaimed(reward.id)
//                )
//            ) {
//                // got this check but haven't claimed yet
//                return ThemeProperties.ALERT_ICON.get(this.quest);
//            }
//        }
//        return questIcon;
    }
}
