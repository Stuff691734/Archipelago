package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.Quest;
import io.github.archipelagomw.ClientStatus;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Quest.class)
public class QuestMixin implements QuestAccessor {
    @Shadow
    private DependencyRequirement dependencyRequirement;

    @Override
    public DependencyRequirement archipelago$getDependencyRequirement() {
        return dependencyRequirement;
    }

    @Inject(
            method = "onCompleted",
            at = @At("RETURN")
    )
    public void sendArchipelagoQuest(QuestProgressEventData<?> data, CallbackInfo ci) {
        Archipelago.LOGGER.info("Quest Completed.");
        if (Archipelago.client.isConnected()) {
            Long quest_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("ftb " + this);
            if (quest_id != null) {
                Archipelago.client.getLocationManager().checkLocation(quest_id);
                if (("ftb " + this).equals(Archipelago.archipelagoPersistentState.slotData.get("final_goal"))) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else {
            Archipelago.archipelagoPersistentState.pendingChecks.add("ftb " + this);
            Archipelago.archipelagoPersistentState.setDirty();
        }
    }
}
