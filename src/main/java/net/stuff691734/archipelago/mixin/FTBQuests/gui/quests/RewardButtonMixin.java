package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftbquests.gui.quests.RewardButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RewardButton.class)
public class RewardButtonMixin {
    @Shadow(remap = false)
    @Final
    public Reward reward;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;isCompleted(Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"), remap = false)
    private boolean modifyRewardAccess(TeamData teamData, QuestObject questObject) {
        assert Minecraft.getInstance().player != null;
        return Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl((Quest) questObject), teamData.isCompleted(questObject))
                && !teamData.isRewardClaimed(Minecraft.getInstance().player.getUUID(), this.reward);
    }

    @Redirect(method = "getWidgetType", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;isCompleted(Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"), remap = false)
    public boolean getWidgetType(TeamData teamData, QuestObject questObject) {
        // required for allowing user to click on quest reward and get reward
        assert Minecraft.getInstance().player != null;
        return Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl((Quest) questObject), teamData.isCompleted(questObject))
                && !teamData.isRewardClaimed(Minecraft.getInstance().player.getUUID(), this.reward);
    }
}
