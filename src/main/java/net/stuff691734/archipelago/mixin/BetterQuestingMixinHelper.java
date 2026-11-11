package net.stuff691734.archipelago.mixin;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.controls.GuiButtonQuestInstance;
import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.JsonHelper;
import betterquesting.core.BetterQuesting;
import betterquesting.misc.UserEntry;
import betterquesting.questing.QuestDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.betterquesting.implementations.BetterQuestingQuestImpl;
import net.stuff691734.archipelagoLib.CheckType;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BetterQuestingMixinHelper {
    public static boolean getResult(EnumLogic logic, int inputs, int total) {
        switch (logic) {
            case AND:
                return inputs >= total;
            case OR:
            case XOR:
                return inputs > 0;
            default:
                return true;
        }
    }

    public static boolean isUnlocked(IQuest quest, boolean original) {
        return Archipelago.logic.isBetterQuestingQuestStartable(new BetterQuestingQuestImpl(quest), original);

    }

    public static void addToTooltip(List<String> tooltip, IQuest quest, UUID playerID) {
        if (!quest.isUnlocked(playerID)) {
            ArrayList<String> list = new ArrayList<>();
            Archipelago.logic.addBetterQuestingDependencyTooltip(list, new BetterQuestingQuestImpl(quest));
            tooltip.add("");
            for (String item : list) {
                tooltip.add(TextFormatting.RED + item);
            }
        }
    }

    public static void sendCheck(IQuest quest) {
        Archipelago.client.sendCheck(CheckType.BETTER_QUESTING.addPrefix(
                String.valueOf(QuestDatabase.INSTANCE.getKey(quest))
        ));
    }

    public static EnumQuestState getState(IQuest quest, UUID uuid) {
        // unclaimed will now have priority instead of completed.
        if (!quest.hasClaimed(uuid) && Archipelago.logic.isBetterQuestingQuestRewardObtained(new BetterQuestingQuestImpl(quest), quest.isComplete(uuid) && !quest.hasClaimed(uuid))) {
            return EnumQuestState.UNCLAIMED;
        }
        if (quest.isComplete(uuid)) {
            return EnumQuestState.COMPLETED;
        }
        if (quest.isUnlocked(uuid)) {
            return EnumQuestState.UNLOCKED;
        } else {
            return EnumQuestState.LOCKED;
        }
    }

    public static boolean canClaim(IQuest quest, boolean original, EntityPlayer player) {
        return Archipelago.logic.isBetterQuestingQuestRewardObtained(new BetterQuestingQuestImpl(quest), original) && !quest.hasClaimed(QuestingAPI.getQuestingUUID(player));
    }

    public static boolean canSubmit(IQuest quest, boolean original, EntityPlayer player) {
        return Archipelago.logic.isBetterQuestingQuestStartable(new BetterQuestingQuestImpl(quest), original) && quest.isUnlocked(QuestingAPI.getQuestingUUID(player));
    }

    public static UserEntry GetUserClaimedEntry(ArrayList<UserEntry> claimedUsers, UUID uuid) {
        for(UserEntry entry : claimedUsers) {
            if (entry.getUUID().equals(uuid)) {
                return entry;
            }
        }

        return null;
    }

    public static JsonObject writeProgress(JsonObject json, ArrayList<UserEntry> claimedUsers) {
        JsonArray claimedJson = new JsonArray();
        for(UserEntry entry : claimedUsers) {
            claimedJson.add(entry.writeToJson(new JsonObject()));
        }

        json.add("claimed", claimedJson);

        return json;
    }

    public static void readProgress(JsonObject json, ArrayList<UserEntry> claimedUsers) {
        claimedUsers.clear();

        for(JsonElement entry : JsonHelper.GetArray(json, "completed")) {
            if (entry != null && entry.isJsonObject()) {
                try {
                    UUID uuid = UUID.fromString(JsonHelper.GetString(entry.getAsJsonObject(), "uuid", ""));
                    UserEntry user = new UserEntry(uuid);
                    user.readFromJson(entry.getAsJsonObject());
                    claimedUsers.add(user);
                } catch (Exception e) {
                    BetterQuesting.logger.log(Level.ERROR, "Unable to load UUID for quest", e);
                }
            }
        }
    }
}
