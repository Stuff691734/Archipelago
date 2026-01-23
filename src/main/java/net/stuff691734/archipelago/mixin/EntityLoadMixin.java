package net.stuff691734.archipelago.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.stuff691734.archipelago.fabricEvents.OnLoad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class EntityLoadMixin {
    @Shadow
    boolean inEntityTick;

    // Call our load event after vanilla has loaded the entity
    @Inject(method = "loadEntityUnchecked", at = @At("TAIL"))
    private void onLoadEntity(Entity entity, CallbackInfo ci) {
        if (!this.inEntityTick) { // Copy vanilla logic, we cannot load entities while the game is ticking entities
            OnLoad.ENTITY_LOAD.invoker().onLoad(entity, (ServerWorld) (Object) this);
        }
    }
}
