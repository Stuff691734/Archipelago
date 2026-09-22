package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.gui.quests.ModpackButton;
import com.feed_the_beast.ftbquests.quest.Chapter;
import com.feed_the_beast.ftbquests.quest.ChapterGroup;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModpackButton.class)
public abstract class ModpackButtonMixin {
    @Inject(method = "hasUnclaimedRewards", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void onClickedHasUnclaimedRewards(ClientQuestFile questFile, CallbackInfoReturnable<Boolean> cir) {
        if (
            Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.quest_checks_give_rewards ||
                !Archipelago.slotData.activated_modules.contains("FTBQuests")
            )
        ) {
            return;
        }
        boolean hasAvailableReward = false;
        for(ChapterGroup group : questFile.chapterGroups) {
            for(Chapter chapter : group.chapters) {
                for(Quest quest : chapter.quests) {
                    assert Minecraft.getInstance().player != null;
                    if (Archipelago.logic.isFTBQuestRewardObtained(new FTBQuestsImpl(quest), PlayerData.get(Minecraft.getInstance().player).isComplete(quest)) &&
                            quest.rewards.stream().anyMatch(
                                    (reward) -> !PlayerData.get(Minecraft.getInstance().player).isRewardClaimed(reward.id)
                            )
                    ) {
                        hasAvailableReward = true;
                    }
                }
            }
        }
        cir.setReturnValue(hasAvailableReward);
    }
}
