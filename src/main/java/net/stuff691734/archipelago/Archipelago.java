package net.stuff691734.archipelago;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Archipelago implements ModInitializer {

    @Override
    public void onInitialize() {
        SheepShearCallback.EVENT.register((player, sheep) -> {
            sheep.setSheared(true);

            // Create diamond item entity at sheep's position.
            ItemStack stack = new ItemStack(Items.DIAMOND);
            ItemEntity itemEntity = new ItemEntity(player.getEntityWorld(), sheep.getX(), sheep.getY(), sheep.getZ(), stack);
            player.getEntityWorld().spawnEntity(itemEntity);

            return ActionResult.FAIL;
        });
    }

    public interface SheepShearCallback {
        Event<SheepShearCallback> EVENT = EventFactory.createArrayBacked(SheepShearCallback.class,
                (listeners) -> (player, sheep) -> {
                    for (SheepShearCallback listener : listeners) {
                        ActionResult result = listener.interact(player, sheep);

                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }

                    return ActionResult.PASS;
                });

        ActionResult interact(PlayerEntity player, SheepEntity sheep);
    }
}
