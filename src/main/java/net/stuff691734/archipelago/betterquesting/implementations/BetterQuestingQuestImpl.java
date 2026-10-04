package net.stuff691734.archipelago.betterquesting.implementations;

import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestInstance;
import betterquesting.questing.QuestLineDatabase;
import net.minecraftforge.fml.common.Loader;
import net.stuff691734.archipelago.bq_standard.BQStandardMethods;
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
        int id = QuestDatabase.INSTANCE.getID(this.quest);
        for (DBEntry<IQuestLine> questLine : QuestLineDatabase.INSTANCE.getEntries()) {
            for (DBEntry<IQuestLineEntry> questEntry : questLine.getValue().getEntries()) {
                if (questEntry.getID() == id) {
                    return String.valueOf(questLine.getID());
                }
            }
        }
        return "0";
    }

    /**
     * Returns the id of this check.
     *
     * @return the id of this check.
     */
    @Override
    public String getId() {
        return String.valueOf(QuestDatabase.INSTANCE.getID(this.quest));

    }

    /**
     * Returns the difficulty of this check.
     *
     * @return the difficulty of this check.
     */
    @Override
    public String getDifficulty() {
        // really not sure why this is deprecated?
        return this.quest.getProperty(NativeProps.MAIN) ? "main" : "other";
    }

    /**
     * Returns whether this check has any dependencies.
     *
     * @return whether this check has any dependencies.
     */
    @Override
    public boolean isRoot() {
        return this.quest.getRequirements().length == 0;
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
        return this.quest.getProperty(NativeProps.NAME);
    }

    /**
     * Method called after check has been received to update visuals of the check.
     */
    @Override
    public void updateVisibility() {
        // TODO: is there anything to run after getting a check?
    }

    @Override
    public String getChapterName() {
        int id = QuestDatabase.INSTANCE.getID(this.quest);
        for (DBEntry<IQuestLine> questLine : QuestLineDatabase.INSTANCE.getEntries()) {
            for (DBEntry<IQuestLineEntry> questEntry : questLine.getValue().getEntries()) {
                if (questEntry.getID() == id) {
                    return QuestLineDatabase.INSTANCE.getValue(questLine.getID()).getProperty(NativeProps.NAME);
                }
            }
        }
        return "";
    }

    @Override
    public int getMinimumDependencies() {
        switch (this.quest.getProperty(NativeProps.LOGIC_QUEST)) {
            case OR:
            case XOR:
                return 1;
            case AND:
                return 0;
            default:
                return -1;
        }
//        QuestInstance;
//        EnumLogic;
    }

    /**
     * Returns a list of this quest's advancement dependencies.
     *
     * @return a list of this quest's advancement dependencies.
     */
    @Override
    public List<String> getAdvancementDependencies() {
        if (Loader.isModLoaded("bq_standard")) {
            return BQStandardMethods.getAdvancementDependencies(this.quest);
        }
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
        for (int dependency : this.quest.getRequirements()) {
            dependencies.add(new BetterQuestingQuestImpl(QuestDatabase.INSTANCE.getValue(dependency)));
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
        return this.quest.getProperty(NativeProps.VISIBILITY) == EnumQuestVisibility.HIDDEN;
    }
}
