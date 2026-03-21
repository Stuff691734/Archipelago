package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuestButton.class)
public class QuestButtonMixin {
    @Shadow
    @Final
    Quest quest;

    // function after quest icon is set but before getting drawn
    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/GuiHelper;setupDrawing()V"))
    public void drawAlertIcon(
            GuiGraphics graphics,
            Theme theme,
            int x, int y,
            int w, int h,
            CallbackInfo ci,
            @Local(name = "questIcon") LocalRef<Icon> icon,
            @Local(name = "teamData") TeamData teamData
    ) {
        if (
            !Archipelago.slotData.isInitiated ||
            (
                Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                Archipelago.slotData.ftb_quest_shape.contains(quest.getShape()) &&
                Archipelago.slotData.quest_checks_give_rewards
            )
        ) {
            // only modify if it is a quest and it is randomized and we randomized rewards
            if (
                Archipelago.archipelagoPersistentState.ftbQuestChecks.getOrDefault(this.quest.getCodeString(), false) &&
                // sadly this undoes some of the optimization done by the ftb team, but teamData.hasUnclaimedRewards returns false when not having access
                this.quest.getRewards().stream().anyMatch(
                    reward -> !teamData.isRewardClaimed(Minecraft.getInstance().player.getUUID(), reward)
                )
            ) {
                // got this check but haven't claimed yet
                icon.set(ThemeProperties.ALERT_ICON.get(this.quest));
            }
        }
    }
}
