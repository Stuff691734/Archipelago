package net.stuff691734.archipelago;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArchipelagoPersistentState extends WorldSavedData {
    private static ArchipelagoPersistentState instance;

    public Map<String, Boolean> advancementChecks = new HashMap<>();
    public Map<String, Boolean> ftbQuestChecks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();
    public List<String> pendingChecks = new ArrayList<>();

    public ArchipelagoPersistentState(String key) {
        super(key);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound nbt) {
        NBTTagCompound archipelagoNbt = new NBTTagCompound();

        NBTTagCompound advancementChecksNbt = new NBTTagCompound();
        advancementChecks.forEach(advancementChecksNbt::putBoolean);

        NBTTagCompound ftbQuestChecksNbt = new NBTTagCompound();
        ftbQuestChecks.forEach(ftbQuestChecksNbt::putBoolean);

        NBTTagCompound slotDataNbt = new NBTTagCompound();
        slotData.forEach(slotDataNbt::putString);

        NBTTagCompound playerLastCheckDataNbt = new NBTTagCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        NBTTagCompound pendingChecksNbt = new NBTTagCompound();
        pendingChecks.forEach(check -> pendingChecksNbt.putString(check, check));

        archipelagoNbt.put("advancement_checks", advancementChecksNbt);
        archipelagoNbt.put("ftb_quest_checks", ftbQuestChecksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.put("pending_checks", pendingChecksNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public void read(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompound("archipelago");

        NBTTagCompound advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.keySet().forEach(key -> advancementChecks.put(key, advancementChecksNbt.getBoolean(key)));

        NBTTagCompound ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.keySet().forEach(key -> ftbQuestChecks.put(key, ftbQuestChecksNbt.getBoolean(key)));

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.keySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.keySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        NBTTagCompound pendingChecksNbt = archipelagoNbt.getCompound("pending_checks");
        pendingChecks.addAll(pendingChecksNbt.keySet());
    }

    public static ArchipelagoPersistentState createNew() {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState(Archipelago.MODID);
        state.advancementChecks = new HashMap<>();
        state.ftbQuestChecks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        state.pendingChecks = new ArrayList<>();
        return state;
    }

    private static ArchipelagoPersistentState getServerState(MinecraftServer server) {
        WorldSavedDataStorage persistentStateManager = server.getWorld(DimensionType.OVERWORLD).getSavedDataStorage();
        if (persistentStateManager == null) {
            return null;
        }

        ArchipelagoPersistentState state = persistentStateManager.get(
            DimensionType.OVERWORLD,
            ArchipelagoPersistentState::new,
            Archipelago.MODID
        );

        if (state == null) {
            state = createNew();
            persistentStateManager.set(DimensionType.OVERWORLD, Archipelago.MODID, state);
        }

        state.setDirty(true);

        return state;
    }

    public static Boolean getAdvancement(String advancement) {
        if (getInstance() != null) {
            return getInstance().advancementChecks.getOrDefault(advancement, false);
        } else {
            return Archipelago.clientState.hasAdvancement(advancement);
        }
    }

    public static Boolean getFtbQuest(String quest) {
        if (getInstance() != null) {
            return getInstance().ftbQuestChecks.getOrDefault(quest, false);
        } else {
            return Archipelago.clientState.hasFtbQuest(quest);
        }
    }

    @Nullable
    public static ArchipelagoPersistentState getInstance() {
        if (Archipelago.getServer() == null) {
            return null;
        }
        if (instance == null) {
            instance = getServerState(Archipelago.getServer());
        }
        return instance;
    }
}
