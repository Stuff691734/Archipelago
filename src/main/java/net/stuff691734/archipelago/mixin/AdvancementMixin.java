package net.stuff691734.archipelago.mixin;

import net.minecraft.advancement.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//@Mixin(SheepEntity.class)
//public class SheepEntityMixin {
//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;sheared(Lnet/minecraft/sound/SoundCategory;)V"), method = "interactMob", cancellable = true)
//    private void onShear(final PlayerEntity player, final Hand hand, final CallbackInfoReturnable<ActionResult> info) {
//        ActionResult result = Archipelago.SheepShearCallback.EVENT.invoker().interact(player, (SheepEntity) (Object) this);
//
//        if (result == ActionResult.FAIL) {
//            info.setReturnValue(result);
//        }
//    }
//}


@Mixin(PlayerAdvancementTracker.class)
public abstract class AdvancementMixin {

    @Shadow
    public abstract AdvancementProgress getProgress(AdvancementEntry advancement);

    @Inject(at = @At("RETURN"), method = "grantCriterion")
    private void onGetAchievement(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (advancement.value().display().isPresent() && this.getProgress(advancement).isDone()) {
            // achievement acquired
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Achievement"));

            // here should be sending a request
            // probably set up python flask server for testing


            // add something to collect messages from flask server


        }
    }
}


// where sopmething uses AdvancementProgress.obtain