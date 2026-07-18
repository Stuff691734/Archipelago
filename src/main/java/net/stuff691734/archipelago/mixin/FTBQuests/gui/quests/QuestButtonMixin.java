package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.gui.quests.ButtonQuest;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.theme.property.ThemeProperties;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
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
        if (Archipelago.logic.isFTBQuestRewardObtained(
                new FTBQuestsImpl(quest),
                quest.rewards.stream().anyMatch(
                        (reward) -> !PlayerData.get(Minecraft.getInstance().player).isRewardClaimed(reward.id)
                )
        )) {
            return ThemeProperties.ALERT_ICON.get(quest);
        }
        return questIcon != ThemeProperties.ALERT_ICON.get(quest) ? questIcon : Icon.EMPTY;
    }
}
