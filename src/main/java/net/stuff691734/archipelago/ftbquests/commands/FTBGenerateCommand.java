package net.stuff691734.archipelago.ftbquests.commands;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.AdvancementTask;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.task.FTBQuestsTasks;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks(boolean removePermaHidden) {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();

        for(Chapter chapter : ServerQuestFile.INSTANCE.chapters) {
            for (Quest quest : chapter.quests) {
                // no longer possible to hide quests. So no removing hidden quests here
                ftbQuestsChecks.put(
                    quest.getCodeString(),
                    new FTBQuestsCheck(
                        quest.getShape(),
                        quest.dependencies.stream()
                                .map((dependency) -> dependency instanceof Task ? ((Task) dependency).quest : dependency)
                                .distinct()
                                .map(Quest::getCodeString).toArray(String[]::new),
                        quest.dependencyRequirement.id,
                        quest.getChapter().getCodeString(),
                        quest.tasks.stream()
                                .filter(task -> task.getType() == FTBQuestsTasks.ADVANCEMENT)
                                .map(task -> ((AdvancementTask) task).advancement)
                                .toArray(String[]::new)
                    )
                );
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
