package net.stuff691734.archipelago.ftbquests.implementations;

import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.task.AdvancementTask;
import com.feed_the_beast.ftbquests.quest.task.Task;
import net.minecraft.util.text.TextComponentTranslation;
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
        return new TextComponentTranslation("ftbquests.unnamed").getUnformattedText();
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
        return this.quest.getAltTitle();
    }

    @Override
    public String getName(ServerInterface server) {
        return this.quest.getTitle();
    }
}
