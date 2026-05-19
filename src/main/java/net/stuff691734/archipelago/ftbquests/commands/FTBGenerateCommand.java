package net.stuff691734.archipelago.ftbquests.commands;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.AdvancementTask;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.ftbquests.quest.task.FTBQuestsTasks;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.DependencyNotation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;

import java.util.*;
import java.util.stream.Collectors;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks(MinecraftServer server , boolean removePermaHidden) {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();

        for(Chapter chapter : ServerQuestFile.INSTANCE.chapters) {
            for (Quest quest : chapter.quests) {
                DependencyNotation dependencies = new DependencyNotation();
                // this uses nested in case it is also with advancements
                DependencyNotation questDependencies = new DependencyNotation();
                if (quest.minRequiredDependencies > 0) {
                    questDependencies.setMinimum(quest.minRequiredDependencies);
                } else if (quest.dependencyRequirement.one) {
                    questDependencies.setMinimum(1);
                } else {
                    questDependencies.setMinimum(0);
                }
                getDependencies(questDependencies, quest);
                dependencies.addNested(questDependencies);

                for (Task task : quest.tasks) {
                    if (task.getType() == FTBQuestsTasks.ADVANCEMENT) {
                        String adv = ((AdvancementTask) task).advancement;
                        if (Utils.isAdvancementId(adv)) {
                            Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(adv));
                            assert advancement != null;
                            if (advancement.getDisplay() != null) {
                                dependencies.addCheck(String.format("adv %s (%s)", adv, advancement.getDisplay().getTitle().getContents()));
                            }
                        }
                    }
                }

                ftbQuestsChecks.put(
                        String.format("ftb %s (%s)", quest.getCodeString(), quest.getTitle()),
                        new FTBQuestsCheck(
                                quest.getShape(),
                                dependencies,
                                String.format("ftb %s (%s)", quest.getChapter().getCodeString(), quest.getChapter().getTitle())
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

    private static void getDependencies(DependencyNotation input, Quest quest) {
        for (QuestObject questObject : quest.dependencies) {
            if (questObject instanceof Chapter) {
                DependencyNotation chapterDependency = new DependencyNotation();
                for (Quest chapterQuest : ((Chapter) questObject).quests) {
                    chapterDependency.addCheck(String.format("ftb %s (%s)", chapterQuest.getCodeString(), chapterQuest.getTitle()));
                }
                input.addNested(chapterDependency);
            } else {
                if (questObject instanceof Task) {
                    questObject = ((Task) questObject).quest;
                }
                input.addCheck(String.format("ftb %s (%s)", questObject.getCodeString(), questObject.getTitle()));
            }
        }
    }
}
