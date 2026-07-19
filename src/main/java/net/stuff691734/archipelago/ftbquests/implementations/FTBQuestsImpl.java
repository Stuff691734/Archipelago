package net.stuff691734.archipelago.ftbquests.implementations;

import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.*;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeEnergyTask;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import net.stuff691734.archipelago.mixin.FTBQuests.quest.task.*;
import net.stuff691734.archipelagoLib.CheckType;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;
import net.stuff691734.archipelagoLib.interfaces.FTBQuestsInterface;
import net.stuff691734.archipelagoLib.interfaces.ServerInterface;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FTBQuestsImpl implements FTBQuestsInterface {
    private final Quest quest;

    public FTBQuestsImpl(Quest quest) {
        this.quest = quest;
    }

    @Override
    public Stream<List<FTBQuestsInterface>> getDependencies() {
        return this.quest.streamDependencies().map((dependency) -> {
            if (dependency instanceof Quest) {
                return Collections.singletonList(new FTBQuestsImpl((Quest) dependency));
            } else if (dependency instanceof Task) {
                return Collections.singletonList(new FTBQuestsImpl(((Task) dependency).getQuest()));
            } else if (dependency instanceof Chapter) {
                return ((Chapter) dependency).getQuests().stream()
                        .map(FTBQuestsImpl::new)
                        .collect(Collectors.toList());
            } else if (dependency instanceof ChapterGroup) {
                return ((ChapterGroup) dependency).getChapters().stream()
                        .flatMap((chapter) -> chapter.getQuests().stream())
                        .map(FTBQuestsImpl::new)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }

    @Override
    public List<String> getAdvancementDependencies() {
        return this.quest.getTasks().stream()
                .filter((task) -> task instanceof AdvancementTask)
                .map((task) -> ((AdvancementTaskAccessor) task).archipelago$advancement().toString())
                .collect(Collectors.toList());
    }

    @Override
    public int getMinimumDependencies() {
        return this.quest.getMinRequiredDependencies();
    }

    @Override
    public boolean hasSingleDependencyRequirement() {
        return ((QuestAccessor)(Object) this.quest).archipelago$getDependencyRequirement().needOnlyOne();
    }

    @Override
    public String getChapterName() {
        if (!this.quest.getChapter().getRawTitle().isEmpty()) {
            return this.quest.getChapter().getRawTitle();
        }
        return Component.translatable("ftbquests.unnamed").getString();
    }

    @Override
    public String getPage() {
        return this.quest.getChapter().getCodeString();
    }

    @Override
    public String getId() {
        return this.quest.getCodeString();
    }

    @Override
    public String getDifficulty() {
        return this.quest.getShape();
    }

    @Override
    public boolean isRoot() {
        return !this.quest.hasDependencies();
    }

    @Override
    public String getName() {
        return this.quest.getAltTitle().getString();
    }

    @Override
    public String getName(ServerInterface server) {
        return this.getTitle(server);
    }

    @Override
    public boolean hasRewards() {
        return !this.quest.getRewards().isEmpty();
    }

    @Override
    public boolean isInvisibleUntilCompleted() {
        return ((QuestAccessor) (Object) this.quest).archipelago$isInvisibleUntilCompleted();
    }

    private String getTitle(ServerInterface server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!this.quest.getRawTitle().isEmpty()) {
            return this.quest.getRawTitle();
        }
        if (!this.quest.getTasks().isEmpty()) {
            Task task = quest.getTasksAsList().get(0);
            if (task.getType() == TaskTypes.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                AdvancementTaskAccessor accessor = (AdvancementTaskAccessor) task1;
                if (Archipelago.client.isValidId(CheckType.ADVANCEMENT, accessor.archipelago$advancement().toString())) {
                    AdvancementInterface advancement = server.getAdvancement(accessor.archipelago$advancement().toString());
                    if (advancement.hasDisplay()) {
                        Component text = (Component.translatable("ftbquests.task.ftbquests.advancement")).append(": ").append(advancement.getName());
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
