package net.stuff691734.archipelago.mixin.FTBQuests.gui.quests;


import com.feed_the_beast.ftbquests.gui.quests.ViewQuestPanel;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.ftbquests.quest.task.Task;
import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.widget.ContextMenuItem;
import net.minecraft.util.text.StringTextComponent;
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
    private void AddArchipelagoDependency(Collection<QuestObject> c, CallbackInfo ci, int hidden, List<ContextMenuItem> contextMenu) {
        if (this.quest != null && this.quest.dependencies == c) {
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
                if (this.quest.dependencies.isEmpty()) {
                    dependencyStream = Stream.of(quest);
                } else {
                    dependencyStream = this.quest.dependencies.stream();
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
            contextMenu.add(new ContextMenuItem(new StringTextComponent(title), Color4I.EMPTY, null));
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 2), remap = false)
    private boolean alwaysHaveDependencies(List<QuestObject> dependencies) {
        return !(
                !Archipelago.slotData.isInitiated ||
                (
                    Archipelago.slotData.activated_modules.contains("FTBQuests") &&
                    Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
                ) ||
                !quest.dependencies.isEmpty());
    }
}
