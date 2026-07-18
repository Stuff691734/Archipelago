package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(QuestButton.class)
public class QuestButtonMixin {
    @Shadow(remap = false)
    public Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/QuestShape;get(Ljava/lang/String;)Ldev/ftb/mods/ftbquests/quest/QuestShape;"), remap = false, name = "qicon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        if (Archipelago.logic.isFTBQuestRewardObtained(
                new FTBQuestsImpl(quest),
                quest.rewards.stream().anyMatch(
                        (reward) -> !TeamData.get(Minecraft.getInstance().player).isRewardClaimed(Minecraft.getInstance().player.getUUID(), reward)
                )
        )) {
            return ThemeProperties.ALERT_ICON.get(quest);
        }
        return questIcon != ThemeProperties.ALERT_ICON.get(quest) ? questIcon : Icon.EMPTY;
    }
}
