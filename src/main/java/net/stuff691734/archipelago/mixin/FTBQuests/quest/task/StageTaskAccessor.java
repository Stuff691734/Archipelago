package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.StageTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StageTask.class)
public interface StageTaskAccessor {
    @Accessor(value = "stage", remap = false)
    String archipelago$stage();
}
