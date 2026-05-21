package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.StructureTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureTask.class)
public interface StructureTaskAccessor {
    @Invoker(value = "getStructure", remap = false)
    String archipelago$getStructure();
}
