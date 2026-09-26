package net.stuff691734.archipelago.mixin.FTBQuests.quest.task;

import dev.architectury.fluid.FluidStack;
import dev.ftb.mods.ftbquests.quest.task.FluidTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidTask.class)
public interface FluidTaskAccessor {
    @Accessor("fluidStack")
    FluidStack archipelago$fluidStack();
}
