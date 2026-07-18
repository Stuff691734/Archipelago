package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.implementations.AdvancementImpl;
import net.stuff691734.archipelago.ftbquests.implementations.FTBQuestsImpl;
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
    private void AddArchipelagoDependency(Collection<QuestObject> c, CallbackInfo ci, int hidden, List<ContextMenuItem> contextMenu) {
        if (this.quest != null && this.quest.dependencies == c) {
            assert Minecraft.getInstance().player != null;
            List<String> items = Archipelago.logic.addDependencies(
                    (advancement) -> new AdvancementImpl(Minecraft.getInstance().player.connection.getAdvancements().getAdvancements().get(new ResourceLocation(advancement))),
                    new FTBQuestsImpl(this.quest)
            );
            for (String item : items) {
                contextMenu.add(new ContextMenuItem(new TextComponent(item), Color4I.EMPTY, null));
            }
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 2), remap = false)
    private boolean alwaysHaveDependencies(List<QuestObject> dependencies) {
        return Archipelago.logic.isFTBQuestRandomized(new FTBQuestsImpl(this.quest));
    }
}
