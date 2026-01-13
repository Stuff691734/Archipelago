package net.stuff691734.archipelago;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;


public class ChecksState extends PersistentState {
    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound archipelagoNbt = new NbtCompound();

        NbtCompound checksNbt = new NbtCompound();
        checks.forEach(checksNbt::putBoolean);

        NbtCompound slotDataNbt = new NbtCompound();
        slotData.forEach(slotDataNbt::putString);

        NbtCompound playerLastCheckDataNbt = new NbtCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public static ChecksState createFromNbt(NbtCompound tag) {
        ChecksState state = new ChecksState();
        NbtCompound archipelagoNbt = tag.getCompound("archipelago");

        NbtCompound checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.getKeys().forEach(key -> state.checks.put(key, checksNbt.getBoolean(key)));

        NbtCompound slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        NbtCompound playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        return state;
    }

    public static ChecksState createNew() {
        ChecksState state = new ChecksState();
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    private static final Type<ChecksState> type = new Type<>(
            ChecksState::createNew,
            ChecksState::createFromNbt,
            null
    );

    public static ChecksState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        ChecksState state = persistentStateManager.getOrCreate(type, Archipelago.MOD_ID);

        state.markDirty();

        return state;
    }
}
