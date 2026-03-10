package net.stuff691734.archipelago.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.minecraft.network.chat.Component;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ftbquests.accessor.QuestAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(ViewQuestPanel.class)
public class FTBQuestsViewQuestPanelMixin {

    @Shadow
    private Quest quest;

    @Inject(method = "showList", at = @At(value = "INVOKE", target = "Ldev/ftb/mods/ftblibrary/ui/BaseScreen;openContextMenu(Ljava/util/List;)Ldev/ftb/mods/ftblibrary/ui/ContextMenu;"))
    private void AddArchipelagoDependency(Collection<QuestObject> c, boolean dependencies, CallbackInfo ci, @Local(name = "contextMenu") List<ContextMenuItem> contextMenu) {
        if (this.quest != null && dependencies) {
            String modules = Archipelago.archipelagoPersistentState.slotData.get("activated_modules");
            String shapes = Archipelago.archipelagoPersistentState.slotData.get("ftb_quest_check_shape");

            if (
                modules != null &&
                shapes != null &&
                (!modules.contains("FTBQuests") || !shapes.contains(this.quest.getShape()))
            ) {
                // modules or shapes being null means not initialized -> show dependency
                // not randomizing ftb quests or not randomizing this type of quest
                return;
            }

            // only do this for dependencies
            String title;
            if (Objects.equals(Archipelago.archipelagoPersistentState.slotData.get("unlock_type"), "tab")) {
                title = "Archipelago Item: ftb " + this.quest.getChapter();
            }
            else if (Objects.equals(Archipelago.archipelagoPersistentState.slotData.get("unlock_type"), "tree")) {
                QuestAccessor questAccessor = (QuestAccessor) (Object) this.quest;
                Stream<QuestObject> dependencyStream = this.quest.streamDependencies();
                if (dependencyStream.findAny().isEmpty()) {
                    dependencyStream = Stream.of(quest);
                }
                // can only be null if quest is null, already checked above
                assert questAccessor != null;
                if (questAccessor.archipelago$getDependencyRequirement().needOnlyOne()) {
                    title = "Archipelago Item: any(" + dependencyStream.map(questObject -> "ftb " + questObject) + ")";
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
