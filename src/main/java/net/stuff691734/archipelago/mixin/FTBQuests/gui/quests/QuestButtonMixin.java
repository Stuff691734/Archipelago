package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import com.feed_the_beast.ftbquests.gui.quests.ButtonQuest;
import com.feed_the_beast.ftbquests.quest.PlayerData;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import net.minecraft.client.Minecraft;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ButtonQuest.class)
public class QuestButtonMixin {
    @Shadow(remap = false)
    public Quest quest;

    // function after quest icon is set but before getting drawn
    @ModifyVariable(method = "draw", at = @At(value = "INVOKE", target = "Lcom/feed_the_beast/ftbquests/quest/QuestShape;get(Ljava/lang/String;)Lcom/feed_the_beast/ftbquests/quest/QuestShape;"), remap = false, name = "qicon")
    public Icon drawAlertIcon(Icon questIcon) {
        assert Minecraft.getInstance().player != null;
        return FTBQuestsMixinHelper.getQuestIcon(this.quest, questIcon, PlayerData.get(Minecraft.getInstance().player));
    }
}
