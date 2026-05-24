package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.StatTask;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatTask.class)
public interface StatTaskAccessor {
    @Accessor(value = "stat", remap = false)
    ResourceLocation archipelago$stat();
}
