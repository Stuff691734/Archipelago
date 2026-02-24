package net.stuff691734.archipelago;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;


public class ChecksState extends WorldSavedData {
    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();

    public ChecksState(String key) {
        super(key);
        checks = new HashMap<>();
        slotData = new HashMap<>();
        playerLastCheck = new HashMap<>();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        CompoundNBT archipelagoNbt = new CompoundNBT();

        CompoundNBT checksNbt = new CompoundNBT();
        checks.forEach(checksNbt::putBoolean);

        CompoundNBT slotDataNbt = new CompoundNBT();
        slotData.forEach(slotDataNbt::putString);

        CompoundNBT playerLastCheckDataNbt = new CompoundNBT();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT archipelagoNbt = tag.getCompound("archipelago");

        CompoundNBT checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.keySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));

        CompoundNBT slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.keySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        CompoundNBT playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.keySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));
    }

    public static ChecksState createNew() {
        return new ChecksState(Archipelago.MODID);
    }

    public static ChecksState getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(DimensionType.OVERWORLD);
        // get World but it has no name
        DimensionSavedDataManager persistentStateManager = world.getSavedData();

        ChecksState state = persistentStateManager.getOrCreate(
                ChecksState::createNew,
                Archipelago.MODID
        );

        state.markDirty();


        return state;
    }
}
