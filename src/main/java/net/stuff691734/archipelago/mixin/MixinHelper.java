package net.stuff691734.archipelago.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.CheckType;

import java.util.Objects;

// methods used by coreMod to help write less bytecode
public class MixinHelper {
    public static boolean shouldBeVisible(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            if (Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(advancement.getDisplay().getFrame().getName())
                )
            ) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static void sendArchipelagoAdvancement(Advancement advancement) {
        Utils.sendCheck(CheckType.ADVANCEMENT.addPrefix(advancement.getId().toString()));
    }

    public static boolean allowAdvancementCompletion(Advancement advancement) {
        return !Utils.shouldAdvancementBeHidden(advancement.getDisplay(), advancement);
    }

    public static AdvancementEntryGui getGuiAdvancementParent(AdvancementEntryGui parent, DisplayInfo displayInfo, Advancement advancement) {
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

    public static AdvancementTabGui getGuiAdvancementTab(AdvancementTabGui tab) {
        if (tab == null) {
            return null;
        }
        if (Archipelago.slotData.isInitiated &&
            (
                !Archipelago.slotData.activated_modules.contains("Advancements") ||
                (Objects.equals(Archipelago.slotData.unlock_type, "tree") && Archipelago.slotData.roots_unlocked)
            )
        ) {
            return tab;
        }
        if (!ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(tab.getAdvancement().getId().toString()))) {
            return null;
        }

        return tab;
    }
}
