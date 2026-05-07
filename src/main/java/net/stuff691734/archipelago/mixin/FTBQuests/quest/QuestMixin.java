package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import io.github.archipelagomw.ClientStatus;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Quest.class)
public abstract class QuestMixin implements QuestAccessor {
    @Shadow(remap = false)
    private DependencyRequirement dependencyRequirement;

    @Override
    public DependencyRequirement archipelago$getDependencyRequirement() {
        return dependencyRequirement;
    }

    @Shadow(remap = false)
    private boolean invisibleUntilCompleted;

    @Override
    public boolean archipelago$isInvisibleUntilCompleted() {
        return invisibleUntilCompleted;
    }

    @Shadow(remap = false)
    protected abstract void checkForDependantCompletion(TeamData data);

    @Override
    public void archipelago$checkForDependantCompletion(TeamData data) {
        this.checkForDependantCompletion(data);
    }

    @Inject(
            method = "onCompleted",
            at = @At("RETURN"),
            remap = false
    )
    public void sendArchipelagoQuest(QuestProgressEventData<?> data, CallbackInfo ci) {
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

    @Inject(method = "lambda$checkForDependantCompletion$1", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;streamDependencies()Ljava/util/stream/Stream;"), remap = false, cancellable = true)
    private static void checkIsCompleted(TeamData data, QuestObject questObject, CallbackInfo ci) {
        // if this gets canceled then skip looking at task completion

        Quest quest = (Quest) questObject; // already verified by code above in checkForDependantCompletion
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
            )
        ) {
            // modules or shapes being null means not initialized -> show dependency
            // not randomizing ftb quests or not randomizing this type of quest
            Archipelago.LOGGER.info("First");
            return;
        }

        // prevents quests from being unlocked
        if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getChapter().getCodeString(), false)) {
                // if player hasn't received quest chapter check prevent them from getting the advancement
                ci.cancel();
                Archipelago.LOGGER.info("Second");
            }
        }
        else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
            QuestAccessor questAccessor = (QuestAccessor) (Object) quest;
            assert questAccessor != null;
            DependencyRequirement requirement = questAccessor.archipelago$getDependencyRequirement();
            if (quest.streamDependencies().findAny().isEmpty() && !Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                // no dependencies, check if it has self
                ci.cancel();
                Archipelago.LOGGER.info("Third");

            }
            if (requirement.needOnlyOne()) {
                if (quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .noneMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need one dependency, check if it has any
                    ci.cancel();
                    Archipelago.LOGGER.info("Fourth");
                }
            } else {
                if (!quest.streamDependencies()
                        .map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency)
                        .allMatch((dependency) -> Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(dependency.getCodeString(), false))
                ) {
                    // need all dependency, check if it has all
                    ci.cancel();
                    Archipelago.LOGGER.info("Fifth");
                }
            }
        }
        // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
        // to do an advancement insanity thing
        else {
            if (!Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(quest.getCodeString(), false)) {
                ci.cancel();
                Archipelago.LOGGER.info("Sixth");
            }
        }
        Archipelago.LOGGER.info("Seventh");
    }
}
