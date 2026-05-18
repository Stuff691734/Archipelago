package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerAdvancements.class)
public interface PlayerAdvancementAccessor {
    @Invoker("ensureVisibility")
    void archipelago$ensureVisibility(Advancement advancement);
}
