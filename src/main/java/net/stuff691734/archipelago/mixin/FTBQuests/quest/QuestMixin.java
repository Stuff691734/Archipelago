package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.DependencyRequirement;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        FTBQuestsMixinHelper.sendArchipelagoQuest((Quest)(Object) this);
    }

    @Inject(method = "lambda$checkForDependantCompletion$1", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;streamDependencies()Ljava/util/stream/Stream;"), remap = false, cancellable = true)
    private static void checkIsCompleted(TeamData data, QuestObject questObject, CallbackInfo ci) {
        if (!FTBQuestsMixinHelper.isQuestStartable(true, (Quest) questObject)) {
            ci.cancel();
        }
    }
}
