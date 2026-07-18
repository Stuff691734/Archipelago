package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

import java.util.Collection;
import java.util.List;

@Mixin(ViewQuestPanel.class)
public class ViewQuestPanelMixin {

    @Shadow
    private Quest quest;

    @Inject(method = "showList", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;"))
    @OnlyIn(Dist.CLIENT)
    private void AddArchipelagoDependency(Collection<QuestObject> c, boolean dependencies, CallbackInfo ci, @Local(name = "contextMenu") List<ContextMenuItem> contextMenu) {
        if (this.quest != null && dependencies) {
            assert Minecraft.getInstance().player != null;
            List<String> items = Archipelago.logic.addDependencies(
                    (advancement) -> new AdvancementImpl(Minecraft.getInstance().player.connection.getAdvancements().getTree().get(new ResourceLocation(advancement))),
                    new FTBQuestsImpl(this.quest)
            );
            for (String item : items) {
                contextMenu.add(new ContextMenuItem(Component.literal(item), Color4I.empty(), null));
            }
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;hasDependencies()Z"), remap = false)
    private boolean alwaysHaveDependencies(Quest quest) {
        return Archipelago.logic.isFTBQuestRandomized(new FTBQuestsImpl(quest));
    }
}
