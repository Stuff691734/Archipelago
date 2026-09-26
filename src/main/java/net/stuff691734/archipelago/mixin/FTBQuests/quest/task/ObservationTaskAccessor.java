package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.ObservationTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ObservationTask.class)
public interface ObservationTaskAccessor {
    @Accessor(value = "toObserve", remap = false)
    String archipelago$toObserve();
}
