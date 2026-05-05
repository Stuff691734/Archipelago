package net.stuff691734.archipelago;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

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
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound archipelagoNbt = new NBTTagCompound();

        NBTTagCompound advancementChecksNbt = new NBTTagCompound();
        advancementChecks.forEach(advancementChecksNbt::setBoolean);

        NBTTagCompound ftbQuestChecksNbt = new NBTTagCompound();
        ftbQuestChecks.forEach(ftbQuestChecksNbt::setBoolean);

        NBTTagCompound slotDataNbt = new NBTTagCompound();
        slotData.forEach(slotDataNbt::setString);

        NBTTagCompound playerLastCheckDataNbt = new NBTTagCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::setInteger);

        NBTTagCompound pendingChecksNbt = new NBTTagCompound();
        pendingChecks.forEach(check -> pendingChecksNbt.setString(check, check));

        archipelagoNbt.setTag("advancement_checks", advancementChecksNbt);
        archipelagoNbt.setTag("ftb_quest_checks", ftbQuestChecksNbt);
        archipelagoNbt.setTag("slot_data", slotDataNbt);
        archipelagoNbt.setTag("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.setTag("pending_checks", pendingChecksNbt);

        nbt.setTag("archipelago", archipelagoNbt);

        return nbt;
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompoundTag("archipelago");

        NBTTagCompound advancementChecksNbt = archipelagoNbt.getCompoundTag("advancement_checks");
        advancementChecksNbt.getKeySet().forEach(key -> advancementChecks.put(key, advancementChecksNbt.getBoolean(key)));

        NBTTagCompound ftbQuestChecksNbt = archipelagoNbt.getCompoundTag("ftb_quest_checks");
        ftbQuestChecksNbt.getKeySet().forEach(key -> ftbQuestChecks.put(key, ftbQuestChecksNbt.getBoolean(key)));

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompoundTag("slot_data");
        slotDataNbt.getKeySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompoundTag("player_last_check");
        playerLastCheckDataNbt.getKeySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInteger(key)));

        NBTTagCompound pendingChecksNbt = archipelagoNbt.getCompoundTag("pending_checks");
        pendingChecks.addAll(pendingChecksNbt.getKeySet());
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
        MapStorage persistentStateManager = server.getWorld(DimensionType.OVERWORLD.getId()).getMapStorage();
        if (persistentStateManager == null) {
            return null;
        }

        ArchipelagoPersistentState state = (ArchipelagoPersistentState) persistentStateManager.getOrLoadData(
            ArchipelagoPersistentState.class,
            Archipelago.MODID
        );

        if (state == null) {
            state = createNew();
            persistentStateManager.setData(Archipelago.MODID, state);
        }

        state.setDirty(true);

        return state;
    }

    public static Boolean getAdvancement(String advancement) {
        if (getInstance() != null) {
            return getInstance().advancementChecks.getOrDefault(advancement, false);
        }
        return false;
    }

    public static Boolean getFtbQuest(String quest) {
        if (getInstance() != null) {
            return getInstance().ftbQuestChecks.getOrDefault(quest, false);
        }
        return false;
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
