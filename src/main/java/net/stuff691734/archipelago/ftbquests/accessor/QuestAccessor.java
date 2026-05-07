package net.stuff691734.archipelago.ftbquests.accessor;

import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.TeamData;

public interface QuestAccessor {
    DependencyRequirement archipelago$getDependencyRequirement();

    boolean archipelago$isInvisibleUntilCompleted();

    void archipelago$checkForDependantCompletion(TeamData data);
}
