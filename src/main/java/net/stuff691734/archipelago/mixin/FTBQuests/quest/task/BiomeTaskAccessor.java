package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.BiomeTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BiomeTask.class)
public interface BiomeTaskAccessor {
    @Invoker(value = "getBiome", remap = false)
    String archipelago$getBiome();
}
