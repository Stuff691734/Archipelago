package net.stuff691734.archipelago.ftbquests.implementations;

import dev.ftb.mods.ftblibrary.util.StringUtils;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ChapterGroup;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.task.*;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeEnergyTask;
import dev.ftb.mods.ftbquests.quest.task.forge.ForgeFluidTask;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.stuff691734.archipelago.Archipelago;
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
        return this.quest.dependencies.stream().map((dependency) -> {
            if (dependency instanceof Quest) {
                return Collections.singletonList(new FTBQuestsImpl((Quest) dependency));
            } else if (dependency instanceof Task) {
                return Collections.singletonList(new FTBQuestsImpl(((Task) dependency).quest));
            } else if (dependency instanceof Chapter) {
                return ((Chapter) dependency).quests.stream()
                        .map(FTBQuestsImpl::new)
                        .collect(Collectors.toList());
            } else if (dependency instanceof ChapterGroup) {
                return ((ChapterGroup) dependency).chapters.stream()
                        .flatMap((chapter) -> chapter.quests.stream())
                        .map(FTBQuestsImpl::new)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }

    @Override
    public List<String> getAdvancementDependencies() {
        return this.quest.tasks.stream()
                .filter((task) -> task instanceof AdvancementTask)
                .map((task) -> ((AdvancementTask) task).advancement.toString())
                .collect(Collectors.toList());
    }

    @Override
    public int getMinimumDependencies() {
        return this.quest.minRequiredDependencies;
    }

    @Override
    public boolean hasSingleDependencyRequirement() {
        return this.quest.dependencyRequirement.one;
    }

    @Override
    public String getChapterName() {
        if (!this.quest.chapter.title.isEmpty()) {
            return this.quest.chapter.title;
        }
        return new TranslatableComponent("ftbquests.unnamed").getString();
    }

    @Override
    public String getPage() {
        return this.quest.chapter.getCodeString();
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
        return this.quest.dependencies.isEmpty();
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
        return !this.quest.rewards.isEmpty();
    }

    @Override
    public boolean isInvisibleUntilCompleted() {
        return this.quest.invisible;
    }

    private String getTitle(ServerInterface server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!this.quest.title.isEmpty()) {
            return this.quest.title;
        }
        if (!this.quest.tasks.isEmpty()) {
            Task task = this.quest.tasks.get(0);
            if (task.getType() == TaskTypes.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                if (Archipelago.client.isValidId(CheckType.ADVANCEMENT, task1.advancement.toString())) {
                    AdvancementInterface advancement = server.getAdvancement(task1.advancement.toString());
                    if (advancement.hasDisplay()) {
                        Component text = (new TranslatableComponent("ftbquests.task.ftbquests.advancement")).append(": ").append(advancement.getName());
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
