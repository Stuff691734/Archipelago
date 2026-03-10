package net.stuff691734.archipelago;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;


public class ArchipelagoPersistentState extends SavedData {
    public Map<String, Boolean> advancementChecks = new HashMap<>();
    public Map<String, Boolean> ftbQuestChecks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();


    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CompoundTag archipelagoNbt = new CompoundTag();

        CompoundTag advancementChecksNbt = new CompoundTag();
        advancementChecks.forEach(advancementChecksNbt::putBoolean);

        CompoundTag ftbQuestChecksNbt = new CompoundTag();
        ftbQuestChecks.forEach(ftbQuestChecksNbt::putBoolean);

        CompoundTag slotDataNbt = new CompoundTag();
        slotData.forEach(slotDataNbt::putString);

        CompoundTag playerLastCheckDataNbt = new CompoundTag();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        archipelagoNbt.put("advancement_checks", advancementChecksNbt);
        archipelagoNbt.put("ftb_quest_checks", ftbQuestChecksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public static ArchipelagoPersistentState createFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState();
        CompoundTag archipelagoNbt = tag.getCompound("archipelago");

        CompoundTag advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.getAllKeys().forEach(key -> state.advancementChecks.put(key, advancementChecksNbt.getBoolean(key)));

        CompoundTag ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.getAllKeys().forEach(key -> state.ftbQuestChecks.put(key, ftbQuestChecksNbt.getBoolean(key)));

        CompoundTag slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getAllKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        CompoundTag playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getAllKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        return state;
    }

    public static ArchipelagoPersistentState createNew() {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState();
        state.advancementChecks = new HashMap<>();
        state.ftbQuestChecks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    private static final Factory<ArchipelagoPersistentState> type = new Factory<>(
            ArchipelagoPersistentState::createNew,
            ArchipelagoPersistentState::createFromNbt
    );

    public static ArchipelagoPersistentState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        ArchipelagoPersistentState state = persistentStateManager.computeIfAbsent(type, Archipelago.MODID);

        state.setDirty();

        return state;
    }
}
