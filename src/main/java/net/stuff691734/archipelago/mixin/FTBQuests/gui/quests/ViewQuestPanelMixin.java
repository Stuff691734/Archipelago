package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
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

@Mixin(ViewQuestPanel.class)
public class ViewQuestPanelMixin {

    @Shadow(remap = false)
    public Quest quest;

    @Inject(
            method = "showList",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;"
            ),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    @OnlyIn(Dist.CLIENT)
    private void AddArchipelagoDependency(Collection<QuestObject> c, boolean dependencies, CallbackInfo ci, int hidden, List<ContextMenuItem> contextMenu) {
        if (this.quest != null && dependencies) {
            FTBQuestsMixinHelper.addDependencies(contextMenu, this.quest);
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;hasDependencies()Z"), remap = false)
    private boolean alwaysHaveDependencies(Quest quest) {
        if (!Archipelago.slotData.isInitiated) {
            return true;
        }
        if (
                Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
        ) {
            if (Archipelago.slotData.roots_unlocked && quest.getDependencies().toList().isEmpty()) {
                return false;
            }
            return true;
        }
        return quest.hasDependencies();
    }
}
