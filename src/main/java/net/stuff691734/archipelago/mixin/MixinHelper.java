package net.stuff691734.archipelago.mixin;

import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.GuiAdvancement;
import net.minecraft.client.gui.advancements.GuiAdvancementTab;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.Utils;
import org.objectweb.asm.tree.*;

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
        if (Archipelago.client.isConnected()) {
            Long advancement_id = Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get("adv " + advancement.getId());
            if (advancement_id != null) {
                Archipelago.client.getLocationManager().checkLocation(advancement_id);
                if (("adv " + advancement.getId()).equals(Archipelago.slotData.final_goal)) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else {
            Archipelago.archipelagoPersistentState.pendingChecks.add("adv " + advancement.getId());
            Archipelago.archipelagoPersistentState.setDirty(true);
        }
    }

    public static boolean preventAdvancement(Advancement advancement) {
        DisplayInfo display = advancement.getDisplay();
        if (display != null) {
            if (
                Archipelago.slotData.isInitiated &&
                (
                    !Archipelago.slotData.activated_modules.contains("Advancements") ||
                    !Archipelago.slotData.advancement_difficulty.contains(display.getFrame().getName())
                )
            ) {
                return true;
            }

            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                Advancement rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(rootAdvancementName, false)) {
                    // if player hasn't received root check prevent them from getting the advancement
                    return false;
                }
            }
            // parent advancement
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
                    // if root check against self
                    if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false)) {
                        return false;
                    }
                } else {
                    // otherwise check against values up tree not including self
                    Advancement checkAdvancement = advancement;
                    // exits when all advancements up the tree have been checked
                    while (checkAdvancement != null) {
                        checkAdvancement = checkAdvancement.getParent();

                        if (checkAdvancement != null) {
                            String checkAdvancementName = checkAdvancement.getId().toString();
                            if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(checkAdvancementName, false)) {
                                return false;
                            }
                        }
                    }
                }
            }
            // not either tab or tree... invalid/notstarted, going to check against self as I eventually want
            // to do an advancement insanity thing
            else {
                if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(advancement.getId().toString(), false)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static GuiAdvancement getGuiAdvancementParent(GuiAdvancement parent, DisplayInfo displayInfo ,Advancement advancement) {
        if (parent == null) {
            return null;
        }
        if (Utils.shouldAdvancementBeHidden(displayInfo, advancement)) {
            return null;
        }
        return parent;
    }

    public static GuiAdvancementTab getGuiAdvancementTab(GuiAdvancementTab tab) {
        if (tab == null) {
            return null;
        }
        if (Archipelago.slotData.isInitiated && !Archipelago.slotData.activated_modules.contains("Advancements")) {
            return tab;
        }
        if (!Archipelago.archipelagoPersistentState.advancementChecks.getOrDefault(tab.getAdvancement().getId().toString(), false)) {
            return null;
        }

        return tab;
    }
}
