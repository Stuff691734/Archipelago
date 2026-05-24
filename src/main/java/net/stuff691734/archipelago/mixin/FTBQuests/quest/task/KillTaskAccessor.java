package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.KillTask;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KillTask.class)
public interface KillTaskAccessor {
    @Accessor(value = "entity", remap = false)
    ResourceLocation archipelago$entity();
}
