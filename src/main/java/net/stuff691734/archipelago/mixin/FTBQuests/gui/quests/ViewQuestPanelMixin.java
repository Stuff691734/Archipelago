package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;


import com.feed_the_beast.ftbquests.gui.quests.PanelViewQuest;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.mods.ftbguilibrary.widget.ContextMenuItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixinHelper.FTBQuestsMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;

@Mixin(PanelViewQuest.class)
public class ViewQuestPanelMixin {

    @Shadow(remap = false)
    public Quest quest;

    @Inject(
            method = "showList",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/feed_the_beast/mods/ftbguilibrary/widget/GuiBase;openContextMenu(Ljava/util/List;)Lcom/feed_the_beast/mods/ftbguilibrary/widget/ContextMenu;"
            ),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    @OnlyIn(Dist.CLIENT)
    private void AddArchipelagoDependency(Collection<QuestObject> c, CallbackInfo ci, int hidden, List<ContextMenuItem> contextMenu) {
        if (this.quest != null && this.quest.dependencies == c) {
            FTBQuestsMixinHelper.addDependencies(contextMenu, this.quest);
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 2), remap = false)
    private boolean alwaysHaveDependencies(List<QuestObject> dependencies) {
        if (!Archipelago.slotData.isInitiated) {
            return false;
        }
        if (
                Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
        ) {
            if (Archipelago.slotData.roots_unlocked) {
                return quest.dependencies.isEmpty();
            }
            return false;
        }
        return quest.dependencies.isEmpty();
    }
}
