package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.ftb.mods.ftbquests.quest.task.AdvancementTask;
import net.minecraft.resources.ResourceLocation;
import net.stuff691734.archipelago.ftbquests.accessor.AdvancementTaskAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AdvancementTask.class)
public class AdvancementTaskMixin implements AdvancementTaskAccessor {
    @Shadow(remap = false)
    private ResourceLocation advancement;

    @Override
    public ResourceLocation archipelago$advancement() {
        return advancement;
    }
}
