package net.stuff691734.archipelago.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public class PlayerDeathMixin {

    @Inject(at = @At(value = "TAIL"), method = "onDeath")
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (!source.canHarmInCreative()) {
            // no looping hopefully
            Archipelago.client.sendDeathlink(
                    Archipelago.client.getMyName(),
                    source.getDeathMessage((EntityPlayerMP)(Object)this).getFormattedText()
            );
        }
    }

}
