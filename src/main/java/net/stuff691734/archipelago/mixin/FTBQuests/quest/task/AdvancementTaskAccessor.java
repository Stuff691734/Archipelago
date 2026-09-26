package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementTask.class)
public interface AdvancementTaskAccessor {
    @Accessor(value = "advancement", remap = false)
    Identifier archipelago$advancement();
}
