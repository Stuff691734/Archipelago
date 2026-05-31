package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import dev.ftb.mods.ftbquests.client.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(QuestButton.class)
public class OldQuestButtonMixin {
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;areDependenciesComplete(Ldev/ftb/mods/ftbquests/quest/Quest;)Z"), remap = false)
    public boolean drawAlertIcon(TeamData teamData, Quest quest) {
        return FTBQuestsMixinHelper.isQuestStartable(teamData.areDependenciesComplete(quest), quest);
    }
}
