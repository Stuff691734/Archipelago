package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.task.*;
import com.feed_the_beast.mods.ftbguilibrary.utils.StringUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixin.FTBQuests.quest.TaskTypeAccessor;
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
            }
            return Collections.emptyList();
        });
    }

    @Override
    public List<String> getAdvancementDependencies() {
        return this.quest.tasks.stream()
                .filter((task) -> task instanceof AdvancementTask)
                .map((task) -> ((AdvancementTask) task).advancement)
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
        return new TranslationTextComponent("ftbquests.unnamed").getString();
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
        return this.quest.getShape().id;
    }

    @Override
    public boolean isRoot() {
        return this.quest.dependencies.isEmpty();
    }

    @Override
    public String getName() {
        return this.quest.getAltTitle();
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
        return false;
    }

    @Override
    public boolean hasDependants() {
        return true;
    }

    private String getTitle(ServerInterface server) {
        // getAltTitle (used for default quest names) is client side only, so I have reimplemented them here *sigh*
        // This is mostly copy and pasted code from
        if (!this.quest.title.isEmpty()) {
            return this.quest.title;
        }
        if (!this.quest.tasks.isEmpty()) {
            Task task = this.quest.tasks.get(0);
            if (task.getType() == FTBQuestsTasks.ADVANCEMENT) {
                AdvancementTask task1 = (AdvancementTask) task;
                if (Archipelago.client.isValidId(CheckType.ADVANCEMENT, task1.advancement)) {
                    AdvancementInterface advancement = server.getAdvancement(task1.advancement);
                    if (advancement.hasDisplay()) {
                        ITextComponent text = (new TranslationTextComponent("ftbquests.task.ftbquests.advancement")).appendText(": ").appendText(advancement.getName());
                        return text.getString();
                    }
                }
                return task.getType().getDisplayName();
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
