package net.stuff691734.archipelago;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.stuff691734.archipelagoLib.CheckType;
import net.stuff691734.archipelagoLib.interfaces.ServerStorageInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArchipelagoPersistentState extends WorldSavedData implements ServerStorageInterface {
    private static ArchipelagoPersistentState instance;

    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();
    public List<String> pendingChecks = new ArrayList<>();

    public ArchipelagoPersistentState(String key) {
        super(key);
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

        NBTTagCompound pendingChecksNbt = new NBTTagCompound();
        pendingChecks.forEach(check -> pendingChecksNbt.putString(check, check));

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.put("pending_checks", pendingChecksNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public void read(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompound("archipelago");

        NBTTagCompound checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.keySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));


        // ************************************************************************************
        // Backwards compat
        // should make it so you can load a world from 2.2.x and it shouldn't break
        NBTTagCompound advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.keySet().forEach(key -> checks.put(CheckType.ADVANCEMENT.addPrefix(key), advancementChecksNbt.getBoolean(key)));

        NBTTagCompound ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.keySet().forEach(key -> checks.put(CheckType.FTB_QUEST.addPrefix(key), ftbQuestChecksNbt.getBoolean(key)));
        // ************************************************************************************

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.keySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.keySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        NBTTagCompound pendingChecksNbt = archipelagoNbt.getCompound("pending_checks");
        pendingChecks.addAll(pendingChecksNbt.keySet());
    }

    public static ArchipelagoPersistentState createNew() {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState(Archipelago.MODID);
        state.checks = new HashMap<>();
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

    public static ArchipelagoPersistentState getInstance(MinecraftServer server) {
        if (instance == null) {
            instance = getServerState(server);
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    @Override
    public boolean hasCheck(String checkName) {
        return this.checks.getOrDefault(checkName, false);
    }

    @Override
    public void setDirty() {
        this.setDirty(true);
    }

    @Override
    public List<String> getPendingChecks() {
        return this.pendingChecks;
    }

    @Override
    public void addPendingCheck(String check) {
        this.pendingChecks.add(check);
    }

    @Override
    public Map<String, String> getSlotData() {
        return this.slotData;
    }

    @Override
    public Map<String, Boolean> getChecks() {
        return this.checks;
    }

    @Override
    public void addCheck(String check) {
        this.checks.put(check, true);
    }

    @Override
    public void updateLastCheck(Long aLong) {
        Archipelago.executeOnServer((server) -> {
            server.getPlayerList().getPlayers().forEach((player) -> {
                if (this.playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0) < aLong) {
                    this.playerLastCheck.put(player.getCachedUniqueIdString(), aLong.intValue());
                }
            });
        });
    }
}
