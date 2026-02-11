package net.stuff691734.archipelago.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class PlayerDeathMixin {

    @Inject(at = @At(value = "TAIL"), method = "die")
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (!source.isBypassInvul()) {
            // no looping hopefully
            Archipelago.client.sendDeathlink(
                    Archipelago.client.getMyName(),
                    source.getLocalizedDeathMessage((ServerPlayer)(Object)this).getString()
            );
        }
    }

}
