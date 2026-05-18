package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(QuestButton.class)
public class QuestButtonMixin {
    @Shadow(remap = false)
    @Final
    Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/GuiHelper;setupDrawing()V"), remap = false, name = "questIcon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        return FTBQuestsMixinHelper.getQuestIcon(this.quest, questIcon, TeamData.get(Minecraft.getInstance().player));
//        if (Archipelago.slotData.isFTBQuestRewardRandomized(this.quest.getShape())) {
//            // only modify if it is a quest and it is randomized and we randomized rewards
//            assert Minecraft.getInstance().player != null; // would've already errored in QuestButton.draw
//            if (
//                Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(this.quest.getCodeString(), false) &&
//                // sadly this undoes some of the optimization done by the ftb team, but teamData.hasUnclaimedRewards returns false when not having access
//                this.quest.rewards.stream().anyMatch(
//                    reward -> !TeamData.get(Minecraft.getInstance().player).isRewardClaimed(Minecraft.getInstance().player.getUUID(), reward)
//                )
//            ) {
//                // got this check but haven't claimed yet
//                return ThemeProperties.ALERT_ICON.get(this.quest);
//            }
//        }
//        return questIcon;
    }
}
