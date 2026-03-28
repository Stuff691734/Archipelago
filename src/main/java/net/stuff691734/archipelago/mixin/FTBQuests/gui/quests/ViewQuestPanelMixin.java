package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ViewQuestPanel.class)
public class ViewQuestPanelMixin {

    @Shadow(remap = false)
    private Quest quest;

    @Inject(
            method = "showList",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;"
            ),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void AddArchipelagoDependency(Collection<QuestObject> c, boolean dependencies, CallbackInfo ci, int hidden, List<ContextMenuItem> contextMenu) {
        if (this.quest != null && dependencies) {
            if (!Archipelago.slotData.isFTBQuestRewardRandomized(this.quest.getShape())) {
                return;
            }

            // only do this for dependencies
            String title;
            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                title = "Archipelago Item: ftb " + this.quest.getChapter();
            }
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                Stream<QuestObject> dependencyStream;
                if (this.quest.getDependencies().findAny().isEmpty()) {
                    dependencyStream = Stream.of(quest);
                } else {
                    dependencyStream = this.quest.getDependencies();
                }
                dependencyStream = dependencyStream.map((dependency) -> dependency instanceof Task ? ((Task)dependency).quest : dependency);
                if (this.quest.dependencyRequirement.one) {
                    title = "Archipelago Item: any(" + dependencyStream.map(questObject -> "ftb " + questObject).collect(Collectors.joining(", ")) + ")";
                }
                else {
                    title = "Archipelago Item: all(" + dependencyStream.map(questObject -> "ftb " + questObject).collect(Collectors.joining(", ")) + ")";
                }
            }
            else {
                title = "Archipelago Item: ftb " + this.quest;
            }
            contextMenu.add(new ContextMenuItem(Component.literal(title), Color4I.EMPTY, null));
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;hasDependencies()Z"), remap = false)
    private boolean alwaysHaveDependencies(Quest quest) {
        return Archipelago.slotData.isFTBQuestRewardRandomized(quest.getShape());
    }
}
