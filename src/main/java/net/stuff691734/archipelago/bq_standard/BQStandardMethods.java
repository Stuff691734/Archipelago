package net.stuff691734.archipelago.bq_standard;

import betterquesting.api.questing.IQuest;
import bq_standard.tasks.TaskAdvancement;

import java.util.List;
import java.util.stream.Collectors;

public class BQStandardMethods {
    public static List<String> getAdvancementDependencies(IQuest quest) {
        return quest.getTasks().getEntries().stream()
                .filter((task) -> task.getValue() instanceof TaskAdvancement)
                .map((task) -> ((TaskAdvancement)task.getValue()).advID.toString())
                .collect(Collectors.toList());

    }
}
