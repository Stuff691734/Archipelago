package net.stuff691734.archipelago.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.stuff691734.archipelago.events.OnDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
    @Inject(at = @At(value = "TAIL"), method = "onDeath")
    private void onDeath(DamageSource source, CallbackInfo ci) {
        OnDeathEvent.EVENT.invoker().interact((ServerPlayerEntity)(Object)this, source);
    }
}
