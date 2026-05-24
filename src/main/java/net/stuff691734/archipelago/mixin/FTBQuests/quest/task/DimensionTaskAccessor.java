package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.DimensionTask;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionTask.class)
public interface DimensionTaskAccessor {
    @Accessor(value = "dimension", remap = false)
    ResourceKey<Level> archipelago$dimension();
}
