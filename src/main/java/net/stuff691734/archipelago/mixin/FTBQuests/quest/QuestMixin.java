package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Quest.class)
public class QuestMixin {
    @Inject(
            method = "onCompleted",
            at = @At("RETURN"),
            remap = false
    )
    public void sendArchipelagoQuest(PlayerData data, List<ServerPlayerEntity> onlineMembers, List<ServerPlayerEntity> notifiedPlayers, CallbackInfo ci) {
        FTBQuestsMixinHelper.sendArchipelagoQuest((Quest)(Object) this);
    }
}
