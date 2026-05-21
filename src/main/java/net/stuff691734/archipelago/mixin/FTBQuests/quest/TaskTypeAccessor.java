package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.task.TaskType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TaskType.class)
public interface TaskTypeAccessor {
    @Accessor(value = "displayName", remap = false)
    String archipelago$getDisplayName();
}
