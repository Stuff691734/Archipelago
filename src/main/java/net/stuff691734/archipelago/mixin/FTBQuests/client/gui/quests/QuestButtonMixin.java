package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QuestButton.class)
public class QuestButtonMixin {
    @Shadow
    @Final
    Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/GuiHelper;setupDrawing()V"), remap = false, name = "questIcon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        if (Archipelago.logic.isFTBQuestRewardObtained(
                new FTBQuestsImpl(quest),
                quest.getRewards().stream().anyMatch(
                        (reward) -> !TeamData.get(Minecraft.getInstance().player).isRewardClaimed(Minecraft.getInstance().player.getUUID(), reward)
                )
        )) {
            return ThemeProperties.ALERT_ICON.get(quest);
        }
        return questIcon != ThemeProperties.ALERT_ICON.get(quest) ? questIcon : Color4I.empty();
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;areDependenciesComplete(Ldev/ftb/mods/ftbquests/quest/Quest;)Z"), remap = false)
    public boolean drawAlertIcon(TeamData teamData, Quest quest) {
        return Archipelago.logic.isFTBQuestCompletable(new FTBQuestsImpl(quest), teamData.areDependenciesComplete(quest));
    }
}
