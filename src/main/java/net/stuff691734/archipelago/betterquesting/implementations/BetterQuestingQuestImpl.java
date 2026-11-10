package net.stuff691734.archipelago.betterquesting.implementations;

import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import net.stuff691734.archipelagoLib.CheckType;
import net.stuff691734.archipelagoLib.interfaces.BetterQuestingInterface;

import java.util.ArrayList;
import java.util.List;

public class BetterQuestingQuestImpl implements BetterQuestingInterface {
    private final IQuest quest;

    public BetterQuestingQuestImpl(IQuest quest) {
        this.quest = quest;
    }

    /**
     * Returns the page for this check.
     *
     * @return the page for this check.
     */
    @Override
    public String getPage() {
        int id = QuestDatabase.INSTANCE.getKey(this.quest);
        for (IQuestLine questLine : QuestLineDatabase.INSTANCE.getAllValues()) {
            if (QuestDatabase.INSTANCE.getAllKeys().contains(id)) {
                return "c-" + QuestLineDatabase.INSTANCE.getKey(questLine);
            }
        }
        return "c-0";
    }

    /**
     * Returns the id of this check.
     *
     * @return the id of this check.
     */
    @Override
    public String getId() {
        return String.valueOf(QuestDatabase.INSTANCE.getKey(this.quest));

    }

    /**
     * Returns the difficulty of this check.
     *
     * @return the difficulty of this check.
     */
    @Override
    public String getDifficulty() {
        // really not sure why this is deprecated?
        return this.quest.getProperties().getProperty(NativeProps.MAIN) ? "main" : "other";
    }

    /**
     * Returns whether this check has any dependencies.
     *
     * @return whether this check has any dependencies.
     */
    @Override
    public boolean isRoot() {
        return this.quest.getPrerequisites().isEmpty();
    }

    /**
     * Returns the type of this check.
     *
     * @return the type of this check.
     */
    @Override
    public CheckType checkType() {
        return CheckType.BETTER_QUESTING;
    }

    /**
     * Returns this checks name.
     *
     * @return this checks name.
     */
    @Override
    public String getName() {
        return this.quest.getUnlocalisedName();
    }

    /**
     * Method called after check has been received to update visuals of the check.
     */
    @Override
    public void updateVisibility() {}

    @Override
    public String getChapterName() {
        int id = QuestDatabase.INSTANCE.getKey(this.quest);
        for (IQuestLine questLine : QuestLineDatabase.INSTANCE.getAllValues()) {
            if (QuestDatabase.INSTANCE.getAllKeys().contains(id)) {
                return questLine.getUnlocalisedName();
            }
        }
        return "";
    }

    @Override
    public int getMinimumDependencies() {
        switch (this.quest.getProperties().getProperty(NativeProps.LOGIC_QUEST)) {
            case OR:
            case XOR:
                return 1;
            case AND:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Returns a list of this quest's advancement dependencies.
     *
     * @return a list of this quest's advancement dependencies.
     */
    @Override
    public List<String> getAdvancementDependencies() {
        return new ArrayList<>();
    }

    /**
     * Returns a list of this quest's dependencies.
     *
     * @return a list of this quest's dependencies.
     */
    @Override
    public List<BetterQuestingInterface> getDependencies() {
        List<BetterQuestingInterface> dependencies = new ArrayList<>();
        for (IQuest dependency : this.quest.getPrerequisites()) {
            dependencies.add(new BetterQuestingQuestImpl(dependency));
        }
        return dependencies;
    }

    /**
     * Returns whether a quest is hidden from the user.
     *
     * @return whether a quest is hidden from the user.
     */
    @Override
    public boolean isHidden() {
        return this.quest.getProperties().getProperty(NativeProps.VISIBILITY) == EnumQuestVisibility.HIDDEN;
    }
}
