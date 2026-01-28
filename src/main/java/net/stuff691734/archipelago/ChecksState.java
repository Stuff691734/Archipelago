package net.stuff691734.archipelago;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;


public class ChecksState extends SavedData {
    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();


    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CompoundTag archipelagoNbt = new CompoundTag();

        CompoundTag checksNbt = new CompoundTag();
        checks.forEach(checksNbt::putBoolean);

        CompoundTag slotDataNbt = new CompoundTag();
        slotData.forEach(slotDataNbt::putString);

        CompoundTag playerLastCheckDataNbt = new CompoundTag();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public static ChecksState createFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        ChecksState state = new ChecksState();
        CompoundTag archipelagoNbt = tag.getCompound("archipelago");

        CompoundTag checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.getAllKeys().forEach(key -> state.checks.put(key, checksNbt.getBoolean(key)));

        CompoundTag slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getAllKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        CompoundTag playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getAllKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        return state;
    }

    public static ChecksState createNew() {
        ChecksState state = new ChecksState();
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    private static final Factory<ChecksState> type = new Factory<>(
            ChecksState::createNew,
            ChecksState::createFromNbt,
            null
    );

    public static ChecksState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        ChecksState state = persistentStateManager.computeIfAbsent(type, Archipelago.MODID);

        state.setDirty();

        return state;
    }
}
