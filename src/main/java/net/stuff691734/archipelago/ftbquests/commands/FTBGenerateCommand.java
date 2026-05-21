package net.stuff691734.archipelago.ftbquests.commands;

import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.*;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeEnergyTask;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeFluidTask;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
                            String adv = ((AdvancementTask) task).advancement.toString();
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
            if (questObject instanceof ChapterGroup) {
                DependencyNotation chapterGroupDependency = new DependencyNotation();
                for (Chapter chapter : ((ChapterGroup) questObject).chapters) {
                    for (Quest chapterGroupQuest : (chapter.quests)) {
                        chapterGroupDependency.addCheck(String.format("ftb %s (%s)", chapterGroupQuest.getCodeString(), getName(chapterGroupQuest, server)));
                    }
                }
                input.addNested(chapterGroupDependency);
            }
            else if (questObject instanceof Chapter) {
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
            if (task.getType() == TaskTypes.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                if (Utils.isAdvancementId(task1.advancement.toString())) {
                    Advancement advancement = server.getAdvancements().getAdvancement(task1.advancement);
                    if (advancement != null && advancement.getDisplay() != null) {
                        Component text = (new TranslatableComponent("ftbquests.task.ftbquests.advancement")).append(": ").append(advancement.getDisplay().getTitle());
                        return text.getString();
                    }
                }
                return task.getType().getDisplayName().getString();
            }
            if (task.getType() == TaskTypes.BIOME) {
                BiomeTask task1 = (BiomeTask) task;
                Component text = (new TranslatableComponent("ftbquests.task.ftbquests.biome")).append(": ").append(task1.biome.location().toString());
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
                Component text = (new TranslatableComponent("ftbquests.task.ftbquests.dimension")).append(": ").append(task1.dimension.location().toString());
                return text.getString();
            }
            if (task.getType() == TaskTypes.ITEM) {
                ItemTask task1 = (ItemTask) task;
                Component text = task1.count > 1L ? (new TextComponent(task1.count + "x ")).append(task1.item.getHoverName()) : (new TextComponent("")).append(task1.item.getHoverName());
                return text.getString();
            }
            if (task.getType() == TaskTypes.KILL) {
                KillTask task1 = (KillTask) task;
                Component text = new TranslatableComponent("ftbquests.task.ftbquests.kill.title", new Object[]{task1.formatMaxProgress(), new TranslatableComponent("entity." + task1.entity.getNamespace() + "." + task1.entity.getPath())});
                return text.getString();
            }
            if (task.getType() == TaskTypes.LOCATION) {
                // does not override
            }
            if (task.getType() == TaskTypes.OBSERVATION) {
                // does not override
            }
            if (task.getType() == TaskTypes.STAGE) {
                StageTask task1 = (StageTask) task;
                Component text = (new TranslatableComponent("ftbquests.task.ftbquests.gamestage")).append(": ").append(task1.stage);
                return text.getString();
            }
            if (task.getType() == TaskTypes.STAT) {
                StatTask task1 = (StatTask) task;
                Component text = new TranslatableComponent("stat." + task1.stat.getNamespace() + "." + task1.stat.getPath());
                return text.getString();
            }
            if (task.getType() == TaskTypes.STRUCTURE) {
                StructureTask task1 = (StructureTask) task;
                Component text = (new TranslatableComponent("ftbquests.task.ftbquests.structure")).append(": ").append(task1.structure.location().toString());
                return text.getString();
            }
            if (task.getType() == TaskTypes.XP) {
                XPTask task1 = (XPTask) task;
                Component text = (new TranslatableComponent("ftbquests.reward.ftbquests.xp_levels")).append(": ").append(task1.formatMaxProgress());
                return text.getString();
            }
            if (task.getType() == ForgeFluidTask.TYPE) {
                ForgeFluidTask task1 = (ForgeFluidTask) task;
                Component text = (new TextComponent(ForgeFluidTask.getVolumeString(task1.amount) + " of ")).append(task1.createFluidStack().getName());
                return text.getString();
            }
            if (task.getType() == ForgeEnergyTask.TYPE) {
                ForgeEnergyTask task1 = (ForgeEnergyTask) task;
                Component text = new TranslatableComponent("ftbquests.task.ftbquests.forge_energy.text", new Object[]{StringUtils.formatDouble((double)task1.value, true)});
                return text.getString();
            }
            return task.getType().getDisplayName().getString();
        }
        return new TranslatableComponent("ftbquests.unnamed").getString();
    }
}
