package net.stuff691734.archipelago.ftbquests.commands;

import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.resources.ResourceLocation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks(boolean removePermaHidden) {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();

        for(ChapterGroup group : ServerQuestFile.INSTANCE.chapterGroups) {
            for(Chapter chapter : group.chapters) {
                for (Quest quest : chapter.quests) {
                    if (removePermaHidden) {
                        if (quest.getDependants().isEmpty() && quest.invisibleUntilCompleted() && quest.getRewards().isEmpty()) {
                            continue;
                        }
                    }
                    ftbQuestsChecks.put(
                        quest.getCodeString(),
                        new FTBQuestsCheck(
                            quest.getShape(),
                            quest.dependencies.stream()
                                    .map((dependency) -> dependency instanceof Task ? ((Task) dependency).quest : dependency)
                                    .distinct()
                                    .map(String::valueOf).toArray(String[]::new),
                            quest.dependencyRequirement.id,
                            quest.getChapter().getCodeString(),
                            quest.tasks.stream()
                                    .filter(task -> task.getType() == TaskTypes.ADVANCEMENT)
                                    .map(task -> ((AdvancementTask) task).advancement)
                                    .map(ResourceLocation::toString)
                                    .toArray(String[]::new)
                        )
                    );
                }
            }
        }

        return ftbQuestsChecks.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1, // use first instance when dealing with conflicts
                    LinkedHashMap::new
                )
            );
    }
}
