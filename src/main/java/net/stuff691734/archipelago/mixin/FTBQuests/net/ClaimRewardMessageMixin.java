package net.stuff691734.archipelago.mixin.FTBQuests.net;

import dev.ftb.mods.ftbquests.net.ClaimRewardMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClaimRewardMessage.class)
public class ClaimRewardMessageMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/TeamData;isCompleted(Ldev/ftb/mods/ftbquests/quest/QuestObject;)Z"))
    private boolean modifyRewardAccess(TeamData teamData, QuestObject questObject) {
        if (
            questObject instanceof Quest &&
            Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl((Quest) questObject), true)
        ) {
            return true;
        }
        return teamData.isCompleted(questObject);
    }
}
