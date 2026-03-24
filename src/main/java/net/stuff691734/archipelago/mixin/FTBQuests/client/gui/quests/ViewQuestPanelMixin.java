package net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ViewQuestPanel.class)
public class ViewQuestPanelMixin {

    @Shadow
    private Quest quest;

    @Inject(method = "showList", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;"))
    private void AddArchipelagoDependency(Collection<QuestObject> c, boolean dependencies, CallbackInfo ci, @Local(name = "contextMenu") List<ContextMenuItem> contextMenu) {
        if (this.quest != null && dependencies) {
            if (
                Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("FTBQuests") ||
                    !Archipelago.slotData.ftb_quest_shape.contains(quest.getShape())
                )
            ) {
                return;
            }

            // only do this for dependencies
            String title;
            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                title = "Archipelago Item: ftb " + this.quest.getChapter();
            }
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                QuestAccessor questAccessor = (QuestAccessor) (Object) this.quest;
                Stream<QuestObject> dependencyStream;
                if (this.quest.streamDependencies().findAny().isEmpty()) {
                    dependencyStream = Stream.of(quest);
                } else {
                    dependencyStream = this.quest.streamDependencies();
                }
                dependencyStream = dependencyStream.map((dependency) -> dependency instanceof Task ? ((Task)dependency).getQuest() : dependency);
                // can only be null if quest is null, already checked above
                assert questAccessor != null;
                if (questAccessor.archipelago$getDependencyRequirement().needOnlyOne()) {
                    title = "Archipelago Item: any(" + dependencyStream.map(questObject -> "ftb " + questObject).collect(Collectors.joining(", ")) + ")";
                }
                else {
                    title = "Archipelago Item: all(" + dependencyStream.map(questObject -> "ftb " + questObject).collect(Collectors.joining(", ")) + ")";
                }
            }
            else {
                title = "Archipelago Item: ftb " + this.quest;
            }
            contextMenu.add(new ContextMenuItem(Component.literal(title), Color4I.empty(), null));
        }
    }

    @Redirect(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftbquests/quest/Quest;hasDependencies()Z"))
    private boolean alwaysHaveDependencies(Quest instance) {
        // always treat as having dependencies
        return true;
    }
}
