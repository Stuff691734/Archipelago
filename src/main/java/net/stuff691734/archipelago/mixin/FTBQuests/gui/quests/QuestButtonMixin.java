package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.gui.quests.QuestButton;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(QuestButton.class)
public class QuestButtonMixin {
    @Shadow(remap = false)
    @Final
    public Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/GuiHelper;setupDrawing()V"), remap = false, name = "questIcon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        return FTBQuestsMixinHelper.getQuestIcon(this.quest, questIcon, TeamData.get(Minecraft.getInstance().player), Minecraft.getInstance().player.getUUID());
    }
}
