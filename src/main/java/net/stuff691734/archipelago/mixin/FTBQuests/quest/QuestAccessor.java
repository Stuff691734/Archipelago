package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Quest.class)
public interface QuestAccessor {
    @Invoker(value = "checkForDependantCompletion", remap = false)
    void archipelago$checkForDependantCompletion(TeamData data);
}
