package net.stuff691734.archipelago;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;


public class ChecksState extends PersistentState {
    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();

    public ChecksState(String key) {
        super(key);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
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

    @Override
    public void fromTag(CompoundTag tag) {
        ChecksState state = new ChecksState(Archipelago.MOD_ID);
        CompoundTag archipelagoNbt = tag.getCompound("archipelago");

        CompoundTag checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.getKeys().forEach(key -> state.checks.put(key, checksNbt.getBoolean(key)));

        CompoundTag slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        CompoundTag playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));
    }

    public static ChecksState createNew() {
        ChecksState state = new ChecksState(Archipelago.MOD_ID);
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    public static ChecksState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(DimensionType.OVERWORLD).getPersistentStateManager();

        ChecksState state = persistentStateManager.getOrCreate(
            ChecksState::createNew,
            Archipelago.MOD_ID
        );

        state.markDirty();

        return state;
    }
}
