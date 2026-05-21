package net.stuff691734.archipelago.ftbquests.commands;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.*;
import com.feed_the_beast.mods.ftbguilibrary.utils.StringUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.DependencyNotation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;
import net.stuff691734.archipelago.mixin.FTBQuests.quest.TaskTypeAccessor;

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
                getDependencies(questDependencies, quest, server);
                dependencies.addNested(questDependencies);

                for (Task task : quest.tasks) {
                    if (task.getType() == FTBQuestsTasks.ADVANCEMENT) {
                        String adv = ((AdvancementTask) task).advancement;
                        if (Utils.isAdvancementId(adv)) {
                            Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(adv));
                            assert advancement != null;
                            if (advancement.getDisplay() != null) {
                                dependencies.addCheck(String.format("adv %s (%s)", adv, advancement.getDisplay().getTitle().getUnformattedComponentText()));
                            }
                        }
                    }
                }

                ftbQuestsChecks.put(
                        String.format("ftb %s (%s)", quest.getCodeString(), getName(quest, server)),
                        new FTBQuestsCheck(
                                quest.getShape().id,
                                dependencies,
                                String.format("ftb %s (%s)", quest.getChapter().getCodeString(), getName(quest.getChapter(), server))
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

    private static void getDependencies(DependencyNotation input, Quest quest, MinecraftServer server) {
        for (QuestObject questObject : quest.dependencies) {
            if (questObject instanceof Chapter) {
                DependencyNotation chapterDependency = new DependencyNotation();
                for (Quest chapterQuest : ((Chapter) questObject).quests) {
                    chapterDependency.addCheck(String.format("ftb %s (%s)", chapterQuest.getCodeString(), getName(chapterQuest, server)));
                }
                input.addNested(chapterDependency);
            } else {
                if (questObject instanceof Task) {
                    questObject = ((Task) questObject).quest;
                }
                input.addCheck(String.format("ftb %s (%s)", questObject.getCodeString(), getName(questObject, server)));
            }
        }
    }

    private static String getName(QuestObject questObject, MinecraftServer server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!questObject.title.isEmpty()) {
            return questObject.title;
        }
        if (questObject instanceof Quest && !((Quest) questObject).tasks.isEmpty()) {
            Task task = ((Quest)questObject).tasks.get(0);
            if (task.getType() == FTBQuestsTasks.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                if (Utils.isAdvancementId(task1.advancement)) {
                    Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(task1.advancement));
                    if (advancement != null && advancement.getDisplay() != null) {
                        ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.advancement")).appendText(": ").appendSibling(advancement.getDisplay().getTitle());
                        return text.getString();
                    }
                }
            }
            if (task.getType() == FTBQuestsTasks.CHECKMARK) {
                // does not override
            }
            if (task.getType() == FTBQuestsTasks.CUSTOM) {
                // does not override
            }
            if (task.getType() == FTBQuestsTasks.DIMENSION) {
                DimensionTask task1 = (DimensionTask) task;
                ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.dimension")).appendText(": ").appendText(task1.dimension);
                return text.getString();
            }
            if (task.getType() == FTBQuestsTasks.ITEM) {
                ItemTask task1 = (ItemTask) task;
                ITextComponent text = task1.count > 1L ? (new StringTextComponent(task1.count + "x ")).appendSibling(task1.item.getDisplayName()) : (new StringTextComponent("")).appendSibling(task1.item.getDisplayName());
                return text.getString();
            }
            if (task.getType() == FTBQuestsTasks.KILL) {
                KillTask task1 = (KillTask) task;
                ITextComponent text = new TranslationTextComponent("ftbquests.task.ftbquests.kill.title", new Object[]{task1.getMaxProgressString(), new TranslationTextComponent("entity." + task1.entity.getNamespace() + "." + task1.entity.getPath())});
                return text.getString();
            }
            if (task.getType() == FTBQuestsTasks.LOCATION) {
                // does not override
            }
            if (task.getType() == FTBQuestsTasks.OBSERVATION) {
                // does not override
            }
            if (task.getType() == FTBQuestsTasks.STAT) {
                StatTask task1 = (StatTask) task;
                return task1.stat.getName();
                // this seems unfinished in this version
            }
            if (task.getType() == FTBQuestsTasks.XP) {
                XPTask task1 = (XPTask) task;
                ITextComponent text = (new TranslationTextComponent("ftbquests.reward.ftbquests.xp_levels")).appendText(": ").appendText(task1.getMaxProgressString());
                return text.getString();
            }
            if (task.getType() == FTBQuestsTasks.FLUID) {
                FluidTask task1 = (FluidTask) task;
                ITextComponent text = (new StringTextComponent(FluidTask.getVolumeString(task1.amount) + " of ")).appendSibling(task1.createFluidStack().getDisplayName());
                return text.getString();
            }
            if (task.getType() == FTBQuestsTasks.FORGE_ENERGY) {
                ForgeEnergyTask task1 = (ForgeEnergyTask) task;
                ITextComponent text = new TranslationTextComponent("ftbquests.task.ftbquests.forge_energy.text", new Object[]{StringUtils.formatDouble((double)task1.value, true)});
                return text.getString();
            }
            // the getDisplayName() function of TaskType uses i18n which is client side for translation
            // here's the code rewritten for TranslationTextComponent
            String displayName = ((TaskTypeAccessor)(Object) task.getType()).archipelago$getDisplayName();
            if (displayName != null) {
                return displayName;
            } else {
                ResourceLocation id = task.getType().getRegistryName();
                return id == null ? "error" : new TranslationTextComponent("ftbquests.task." + id.getNamespace() + '.' + id.getPath(), new Object[0]).getString();
            }
        }
        return new TranslationTextComponent("ftbquests.unnamed").getString();
    }
}
