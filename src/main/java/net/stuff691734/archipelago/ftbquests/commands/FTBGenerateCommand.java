package net.stuff691734.archipelago.ftbquests.commands;

import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.task.*;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeEnergyTask;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.DependencyNotation;
import net.stuff691734.archipelago.archipelagoData.FTBQuestsCheck;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import net.stuff691734.archipelago.mixin.FTBQuests.quest.task.*;

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
            getDependencies(questDependencies, quest, server);
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
                String.format("ftb %s (%s)", quest.getCodeString(), getName(quest, server)),
                new FTBQuestsCheck(
                        quest.getShape(),
                        dependencies,
                        String.format("ftb %s (%s)", quest.getChapter().getCodeString(), getName(quest.getChapter(), server))
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

    private static void getDependencies(DependencyNotation input, Quest quest, MinecraftServer server) {
        quest.streamDependencies().forEach((questObject) ->  {
            if (questObject instanceof ChapterGroup) {
                DependencyNotation chapterGroupDependency = new DependencyNotation();

                for (Chapter chapter : ((ChapterGroup) questObject).getChapters()) {
                    for (Quest chapterGroupQuest : (chapter.getQuests())) {
                        chapterGroupDependency.addCheck(String.format("ftb %s (%s)", chapterGroupQuest.getCodeString(), getName(chapterGroupQuest, server)));
                    }
                }
                input.addNested(chapterGroupDependency);
            }
            else if (questObject instanceof Chapter) {
                DependencyNotation chapterDependency = new DependencyNotation();
                for (Quest chapterQuest : ((Chapter) questObject).getQuests()) {
                    chapterDependency.addCheck(String.format("ftb %s (%s)", chapterQuest.getCodeString(), getName(chapterQuest, server)));
                }
                input.addNested(chapterDependency);
            } else {
                if (questObject instanceof Task) {
                    questObject = ((Task) questObject).getQuest();
                }
                input.addCheck(String.format("ftb %s (%s)", questObject.getCodeString(), getName(questObject, server)));
            }
        });
    }

    public static String getName(QuestObject questObject, MinecraftServer server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!questObject.getRawTitle().isEmpty()) {
            return questObject.getRawTitle();
        }
        if (questObject instanceof Quest && !((Quest) questObject).getTasks().isEmpty()) {
            Task task = ((Quest)questObject).getTasksAsList().get(0);
            if (task.getType() == TaskTypes.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                AdvancementTaskAccessor accessor = (AdvancementTaskAccessor) task1;
                if (Utils.isAdvancementId(accessor.archipelago$advancement().toString())) {
                    Advancement advancement = server.getAdvancements().getAdvancement(accessor.archipelago$advancement());
                    if (advancement != null && advancement.getDisplay() != null) {
                        Component text = (Component.translatable("ftbquests.task.ftbquests.advancement")).append(": ").append(advancement.getDisplay().getTitle());
                        return text.getString();
                    }
                }
                return task.getType().getDisplayName().getString();
            }
            if (task.getType() == TaskTypes.BIOME) {
                BiomeTask task1 = (BiomeTask) task;
                BiomeTaskAccessor accessor = (BiomeTaskAccessor) task1;
                Component text = (Component.translatable("ftbquests.task.ftbquests.biome")).append(": ").append(accessor.archipelago$getBiome());
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
                DimensionTaskAccessor accessor = (DimensionTaskAccessor) task1;
                Component text = (Component.translatable("ftbquests.task.ftbquests.dimension")).append(": ").append(accessor.archipelago$dimension().location().toString());
                return text.getString();
            }
            if (task.getType() == TaskTypes.ITEM) {
                ItemTask task1 = (ItemTask) task;
                Component text = task1.getMaxProgress() > 1L ? (Component.literal(task1.getMaxProgress() + "x ")).append(task1.getItemStack().getHoverName()) : (Component.literal("")).append(task1.getItemStack().getHoverName());
                return text.getString();
            }
            if (task.getType() == TaskTypes.KILL) {
                KillTask task1 = (KillTask) task;
                KillTaskAccessor accessor = (KillTaskAccessor) task1;
                Component text = Component.translatable("ftbquests.task.ftbquests.kill.title", new Object[]{task1.formatMaxProgress(), Component.translatable("entity." + accessor.archipelago$entity().getNamespace() + "." + accessor.archipelago$entity().getPath())});
                return text.getString();
            }
            if (task.getType() == TaskTypes.LOCATION) {
                // does not override
            }
            if (task.getType() == TaskTypes.OBSERVATION) {
                ObservationTask task1 = (ObservationTask) task;
                ObservationTaskAccessor accessor = (ObservationTaskAccessor) task1;
                Component text = Component.translatable("ftbquests.task.ftbquests.observation").append(": ").append(accessor.archipelago$toObserve());
                return text.getString();
            }
            if (task.getType() == TaskTypes.STAGE) {
                StageTask task1 = (StageTask) task;
                StageTaskAccessor accessor = (StageTaskAccessor) task1;
                Component text = (Component.translatable("ftbquests.task.ftbquests.gamestage")).append(": ").append(accessor.archipelago$stage());
                return text.getString();
            }
            if (task.getType() == TaskTypes.STAT) {
                StatTask task1 = (StatTask) task;
                StatTaskAccessor accessor = (StatTaskAccessor) task1;
                Component text = Component.translatable("stat." + accessor.archipelago$stat().getNamespace() + "." + accessor.archipelago$stat().getPath());
                return text.getString();
            }
            if (task.getType() == TaskTypes.STRUCTURE) {
                StructureTask task1 = (StructureTask) task;
                StructureTaskAccessor structureTaskAccessor = (StructureTaskAccessor) task1;
                Component text = (Component.translatable("ftbquests.task.ftbquests.structure")).append(": ").append(structureTaskAccessor.archipelago$getStructure());
                return text.getString();
            }
            if (task.getType() == TaskTypes.XP) {
                XPTask task1 = (XPTask) task;
                Component text = (Component.translatable("ftbquests.reward.ftbquests.xp_levels")).append(": ").append(task1.formatMaxProgress());
                return text.getString();
            }
            if (task.getType() == TaskTypes.FLUID) {
                FluidTask task1 = (FluidTask) task;
                Component text = (Component.literal(FluidTask.getVolumeString(task1.getMaxProgress()) + " of ")).append(task1.createFluidStack().getName());
                return text.getString();
            }
            if (task.getType() == ForgeEnergyTask.TYPE) {
                ForgeEnergyTask task1 = (ForgeEnergyTask) task;
                Component text = Component.translatable("ftbquests.task.ftbquests.forge_energy.text", new Object[]{StringUtils.formatDouble((double)task1.getValue(), true)});
                return text.getString();
            }
            return task.getType().getDisplayName().getString();
        }
        return Component.translatable("ftbquests.unnamed").getString();
    }
}
