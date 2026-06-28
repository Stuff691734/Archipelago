package net.stuff691734.archipelago.mixinHelper;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;

import java.util.Objects;

// methods used by Mixin to make code more consistent between coremods and mixins
public class MixinHelper {
    public static boolean shouldBeVisible(Advancement advancement) {
        if (advancement.display().isPresent()) {
            if (Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(advancement.display().get().getFrame().getName())
                )
            ) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static void sendArchipelagoAdvancement(AdvancementHolder advancement) {
        Utils.sendCheck(CheckType.ADVANCEMENT.addPrefix(advancement.id().toString()));
    }

    public static boolean allowAdvancementCompletion(AdvancementHolder advancement, AdvancementNode advancementNode) {
        if (advancement.value().display().isPresent()) {
            return !Utils.shouldAdvancementBeHidden(advancement.value().display().get(), advancementNode);
        }
        return true;
    }

    public static AdvancementWidget getGuiAdvancementParent(AdvancementWidget parent, DisplayInfo displayInfo, AdvancementNode advancement) {
        if (parent == null) return null;
        if (Archipelago.slotData.isInitiated) {
            if (
                Archipelago.slotData.activated_modules.contains("Advancements") &&
                !Archipelago.slotData.advancement_difficulty.contains(displayInfo.getFrame().getName())
            ) {
                return null;
            }
            else if (!Archipelago.slotData.activated_modules.contains("Advancements")) {
                return displayInfo.isHidden() ? null : parent;
            }
        }
        if (Utils.shouldAdvancementBeHidden(displayInfo, advancement)) {
            return null;
        }
        return parent;
    }

    public static AdvancementTab getGuiAdvancementTab(AdvancementTab tab) {
        if (tab == null) {
            return null;
        }
        if (Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("Advancements") ||
                Archipelago.slotData.roots_unlocked
            )
        ) {
            return tab;
        }
        if (!ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(tab.getRootNode().holder().id().toString()))) {
            return null;
        }

        return tab;
    }
}