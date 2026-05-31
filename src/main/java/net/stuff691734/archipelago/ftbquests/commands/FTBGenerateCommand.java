package net.stuff691734.archipelago.ftbquests.commands;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.ChapterGroup;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.ftbquests.quest.task.*;
import com.feed_the_beast.ftbquests.quest.task.forge.ForgeEnergyTask;
import com.feed_the_beast.ftbquests.quest.task.forge.ForgeFluidTask;
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

import java.util.*;
import java.util.stream.Collectors;

public class FTBGenerateCommand {
    public static Map<String, FTBQuestsCheck> generateFTBChecks(MinecraftServer server , boolean removePermaHidden) {
        Map<String, FTBQuestsCheck> ftbQuestsChecks = new HashMap<>();

        for(ChapterGroup group : ServerQuestFile.INSTANCE.chapterGroups) {
            for(Chapter chapter : group.chapters) {
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
                        if (task.getType() == TaskTypes.ADVANCEMENT) {
                            String adv = ((AdvancementTask) task).advancement;
                            if (Utils.isAdvancementId(adv)) {
                                Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(adv));
                                assert advancement != null;
                                if (advancement.getDisplay() != null) {
                                    dependencies.addCheck(String.format("adv %s (%s)", adv, advancement.getDisplay().getTitle().getString()));
                                }
                            }
                        }
                    }

                    ftbQuestsChecks.put(
                        String.format("ftb %s (%s)", quest.getCodeString(), getName(quest, server)),
                        new FTBQuestsCheck(
                            quest.getShape(),
                            dependencies,
                            String.format("ftb %s (%s)", quest.getChapter().getCodeString(), getName(quest.getChapter(), server))
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

    public static String getName(QuestObject questObject, MinecraftServer server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!questObject.title.isEmpty()) {
            return questObject.title;
        }
        if (questObject instanceof Quest && !((Quest) questObject).tasks.isEmpty()) {
            Task task = ((Quest)questObject).tasks.get(0);
            if (task.getType() == TaskTypes.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                if (Utils.isAdvancementId(task1.advancement)) {
                    Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(task1.advancement));
                    if (advancement != null && advancement.getDisplay() != null) {
                        ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.advancement")).append(": ").append(advancement.getDisplay().getTitle());
                        return text.getString();
                    }
                }
                return task.getType().getDisplayName().getString();
            }
            if (task.getType() == TaskTypes.BIOME) {
                BiomeTask task1 = (BiomeTask) task;
                ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.biome")).append(": ").append(task1.biome.location().toString());
                return text.getString();

            }
            if (task.getType() == TaskTypes.CHECKMARK) {
                // does not override
            }
            if (task.getType() == TaskTypes.CUSTOM) {
                // does not override
            }
            if (task.getType() == TaskTypes.DIMENSION) {
                DimensionTask task1 = (DimensionTask) task;
                ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.dimension")).append(": ").append(task1.dimension.location().toString());
                return text.getString();
            }
            if (task.getType() == TaskTypes.ITEM) {
                ItemTask task1 = (ItemTask) task;
                ITextComponent text = task1.count > 1L ? (new StringTextComponent(task1.count + "x ")).append(task1.item.getHoverName()) : (new StringTextComponent("")).append(task1.item.getHoverName());
                return text.getString();
            }
            if (task.getType() == TaskTypes.KILL) {
                KillTask task1 = (KillTask) task;
                ITextComponent text = new TranslationTextComponent("ftbquests.task.ftbquests.kill.title", new Object[]{task1.getMaxProgressString(), new TranslationTextComponent("entity." + task1.entity.getNamespace() + "." + task1.entity.getPath())});
                return text.getString();
            }
            if (task.getType() == TaskTypes.LOCATION) {
                // does not override
            }
            if (task.getType() == TaskTypes.OBSERVATION) {
                // does not override
            }
            if (task.getType() == TaskTypes.STAT) {
                StatTask task1 = (StatTask) task;
                ITextComponent text = new TranslationTextComponent("stat." + task1.stat.getNamespace() + "." + task1.stat.getPath());
                return text.getString();
            }
            if (task.getType() == TaskTypes.XP) {
                XPTask task1 = (XPTask) task;
                ITextComponent text = (new TranslationTextComponent("ftbquests.reward.ftbquests.xp_levels")).append(": ").append(task1.getMaxProgressString());
                return text.getString();
            }
            if (task.getType() == TaskTypes.FLUID) {
                ForgeFluidTask task1 = (ForgeFluidTask) task;
                ITextComponent text = (new StringTextComponent(ForgeFluidTask.getVolumeString(task1.amount) + " of ")).append(task1.createFluidStack().getName());
                return text.getString();
            }
            if (task.getType() == TaskTypes.FORGE_ENERGY) {
                ForgeEnergyTask task1 = (ForgeEnergyTask) task;
                ITextComponent text = new TranslationTextComponent("ftbquests.task.ftbquests.forge_energy.text", new Object[]{StringUtils.formatDouble((double)task1.value, true)});
                return text.getString();
            }
            return task.getType().getDisplayName().getString();
        }
        return new TranslationTextComponent("ftbquests.unnamed").getString();
    }
}
