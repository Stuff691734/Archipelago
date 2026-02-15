package net.stuff691734.archipelago;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.SaveDataMemoryStorage;
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
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound archipelagoNbt = new NBTTagCompound();

        NBTTagCompound checksNbt = new NBTTagCompound();
        checks.forEach(checksNbt::setBoolean);

        NBTTagCompound slotDataNbt = new NBTTagCompound();
        slotData.forEach(slotDataNbt::setString);

        NBTTagCompound playerLastCheckDataNbt = new NBTTagCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::setInteger);

        archipelagoNbt.setTag("checks", checksNbt);
        archipelagoNbt.setTag("slot_data", slotDataNbt);
        archipelagoNbt.setTag("player_last_check", playerLastCheckDataNbt);

        nbt.setTag("archipelago", archipelagoNbt);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompoundTag("archipelago");

        NBTTagCompound checksNbt = archipelagoNbt.getCompoundTag("checks");
        checksNbt.getKeySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompoundTag("slot_data");
        slotDataNbt.getKeySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompoundTag("player_last_check");
        playerLastCheckDataNbt.getKeySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInteger(key)));
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
        World world = server.getWorld(DimensionType.OVERWORLD.getId());
        MapStorage persistentStateManager = world.getMapStorage();
        if (persistentStateManager == null) {
            return null;
        }
//
        ChecksState state = (ChecksState) persistentStateManager.getOrLoadData(
                ChecksState.class,
                Archipelago.MODID);

        if (state == null) {
            state = createNew();
        }

        state.markDirty();


        return state;
    }
}
