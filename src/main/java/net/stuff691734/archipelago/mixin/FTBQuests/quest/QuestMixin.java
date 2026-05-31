package net.stuff691734.archipelago.mixin.FTBQuests.quest;

import dev.ftb.mods.ftbquests.events.QuestProgressEventData;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Quest.class)
public class QuestMixin {
    @Inject(
            method = "onCompleted",
            at = @At("RETURN"),
            remap = false
    )
    public void sendArchipelagoQuest(QuestProgressEventData<?> data, CallbackInfo ci) {
        FTBQuestsMixinHelper.sendArchipelagoQuest((Quest)(Object) this);
    }

    @Inject(method = "lambda$checkForDependantCompletion$1", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;getDependencies()Ljava/util/stream/Stream;"), remap = false, cancellable = true)
    private static void checkIsCompleted(TeamData data, QuestObject questObject, CallbackInfo ci) {
        if (!FTBQuestsMixinHelper.isQuestStartable(true, (Quest) questObject)) {
            ci.cancel();
        }
    }
}
