package net.stuff691734.archipelago.ftbquests.commands;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.DependencyNotation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;
import net.stuff691734.archipelago.ftbquests.accessor.AdvancementTaskAccessor;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks(MinecraftServer server , boolean removePermaHidden) {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();

        FTBQuestsAPI.api().getQuestFile(false).forAllQuests((quest) -> {
            QuestAccessor questAccessor = (QuestAccessor) (Object) quest;
            if (removePermaHidden) {
                if (quest.getDependants().isEmpty() && questAccessor.archipelago$isInvisibleUntilCompleted() && quest.getRewards().isEmpty()) {
                    return;
                }
            }
            DependencyNotation dependencies = new DependencyNotation();
            // this uses nested in case it is also with advancements
            DependencyNotation questDependencies = new DependencyNotation();
            if (quest.getMinRequiredDependencies() > 0) {
                questDependencies.setMinimum(quest.getMinRequiredDependencies());
            } else if (questAccessor.archipelago$getDependencyRequirement().needOnlyOne()) {
                questDependencies.setMinimum(1);
            } else {
                questDependencies.setMinimum(0);
            }
           getDependencies(questDependencies, quest);
           dependencies.addNested(questDependencies);
           for (Task task : quest.getTasks()) {
                if (task.getType() == TaskTypes.ADVANCEMENT) {
                    ResourceLocation adv = ((AdvancementTaskAccessor) task).archipelago$advancement();
                    if (Utils.isAdvancementId(adv.toString())) {
                        Advancement advancement = server.getAdvancements().getAdvancement(adv);
                        assert advancement != null;
                        if (advancement.getDisplay() != null) {
                            dependencies.addCheck(String.format("adv %s (%s)", adv, advancement.getDisplay().getTitle().getString()));
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
        });

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
        quest.streamDependencies().forEach((questObject) ->  {
            if (questObject instanceof ChapterGroup) {
                DependencyNotation chapterGroupDependency = new DependencyNotation();
                for (Chapter chapter : ((ChapterGroup) questObject).getChapters()) {
                    for (Quest chapterGroupQuest : (chapter.getQuests())) {
                        chapterGroupDependency.addCheck(String.format("ftb %s (%s)", chapterGroupQuest.getCodeString(), chapterGroupQuest.getTitle()));
                    }
                }
                input.addNested(chapterGroupDependency);
            }
            if (questObject instanceof Chapter) {
                DependencyNotation chapterDependency = new DependencyNotation();
                for (Quest chapterQuest : ((Chapter) questObject).getQuests()) {
                    chapterDependency.addCheck(String.format("ftb %s (%s)", chapterQuest.getCodeString(), chapterQuest.getTitle()));
                }
                input.addNested(chapterDependency);
            } else {
                if (questObject instanceof Task) {
                    questObject = ((Task) questObject).getQuest();
                }
                input.addCheck(String.format("ftb %s (%s)", questObject.getCodeString(), questObject.getTitle()));
            }
        });
    }
}
