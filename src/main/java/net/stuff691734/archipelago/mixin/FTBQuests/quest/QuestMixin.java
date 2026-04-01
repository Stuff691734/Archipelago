package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import io.github.archipelagomw.ClientStatus;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.stuff691734.archipelago.Archipelago;
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
        Archipelago.LOGGER.info("Quest Completed.");
        if (Archipelago.client.isConnected()) {
            Long quest_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("ftb " + this);
            if (quest_id != null) {
                Archipelago.client.getLocationManager().checkLocation(quest_id);
                if (("ftb " + this).equals(Archipelago.slotData.final_goal)) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else {
            Archipelago.archipelagoPersistentState.pendingChecks.add("ftb " + this);
            Archipelago.archipelagoPersistentState.setDirty();
        }
    }
}
