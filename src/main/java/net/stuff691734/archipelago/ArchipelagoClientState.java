package net.stuff691734.archipelago;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArchipelagoClientState {
    public Set<String> advancements;
    public Set<String> quests;

    public ArchipelagoClientState() {
        this.advancements = new HashSet<>();
        this.quests = new HashSet<>();
    }

    public void setAdvancements(String[] advancements) {
        this.advancements.addAll(Arrays.asList(advancements));
    }

    public void setQuests(String[] quests) {
        this.quests.addAll(Arrays.asList(quests));
    }

    public void addAdvancement(String advancement) {
        this.advancements.add(advancement);
    }

    public void addQuest(String quest) {
        this.quests.add(quest);
    }

    public boolean hasAdvancement(String advancement) {
        return this.advancements.contains(advancement);
    }

    public boolean hasFtbQuest(String quest) {
        return this.quests.contains(quest);
    }
}
