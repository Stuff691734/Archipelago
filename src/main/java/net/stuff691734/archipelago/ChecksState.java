package net.stuff691734.archipelago;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

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
    public NBTTagCompound write(NBTTagCompound nbt) {
        NBTTagCompound archipelagoNbt = new NBTTagCompound();

        NBTTagCompound checksNbt = new NBTTagCompound();
        checks.forEach(checksNbt::putBoolean);

        NBTTagCompound slotDataNbt = new NBTTagCompound();
        slotData.forEach(slotDataNbt::putString);

        NBTTagCompound playerLastCheckDataNbt = new NBTTagCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    @Override
    public void read(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompound("archipelago");

        NBTTagCompound checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.keySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.keySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.keySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));
    }

    public static ChecksState createNew() {
        ChecksState state = new ChecksState(Archipelago.MODID);
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    public static ChecksState getServerState(MinecraftServer server) {
        // get World but it has no name
        DimensionSavedDataManager persistentStateManager = new DimensionSavedDataManager(DimensionType.OVERWORLD,null);
////        server.getWorld(DimensionType.OVERWORLD).
//        DimensionSavedDataManager a = new DimensionSavedDataManager(DimensionType.OVERWORLD,null);
//
        ChecksState state = persistentStateManager.getOrLoadData(
                ChecksState::new,
                Archipelago.MODID);

        if (state == null) {
            state = createNew();
        }

        state.markDirty();


        return state;
    }
}
