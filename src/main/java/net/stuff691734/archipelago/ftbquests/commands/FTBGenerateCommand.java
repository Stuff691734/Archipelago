package net.stuff691734.archipelago.ftbquests.commands;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;
import net.stuff691734.archipelago.ftbquests.accessor.AdvancementTaskAccessor;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;

import java.util.HashMap;
import java.util.Map;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks() {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();
        FTBQuestsAPI.api().getQuestFile(true).forAllQuests((quest) -> {
            QuestAccessor questAccessor = (QuestAccessor) (Object) quest;

            ftbQuestsChecks.put(
                    quest.getCodeString(),
                    new FTBQuestsCheck(
                            quest.getShape(),
                            quest.streamDependencies()
                                    .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                                    .distinct()
                                    .map(String::valueOf).toArray(String[]::new),
                            questAccessor.archipelago$getDependencyRequirement().getId(),
                            quest.getChapter().getCodeString(),
                            quest.getTasks().stream()
                                    .filter(task -> task.getType() == TaskTypes.ADVANCEMENT)
                                    .map(task -> ((AdvancementTaskAccessor)task).archipelago$advancement())
                                    .map(ResourceLocation::toString)
                                    .toArray(String[]::new)
                    )
            );
        });
        return ftbQuestsChecks;
    }
}
