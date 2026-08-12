package net.stuff691734.archipelago;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
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
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound archipelagoNbt = new NBTTagCompound();

        NBTTagCompound checksNbt = new NBTTagCompound();
        checks.forEach(checksNbt::setBoolean);

        NBTTagCompound slotDataNbt = new NBTTagCompound();
        slotData.forEach(slotDataNbt::setString);

        NBTTagCompound playerLastCheckDataNbt = new NBTTagCompound();
        playerLastCheck.forEach(playerLastCheckDataNbt::setInteger);

        NBTTagCompound pendingChecksNbt = new NBTTagCompound();
        pendingChecks.forEach(check -> pendingChecksNbt.setString(check, check));

        archipelagoNbt.setTag("checks", checksNbt);
        archipelagoNbt.setTag("slot_data", slotDataNbt);
        archipelagoNbt.setTag("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.setTag("pending_checks", pendingChecksNbt);

        nbt.setTag("archipelago", archipelagoNbt);

        return nbt;
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagCompound archipelagoNbt = tag.getCompoundTag("archipelago");

        NBTTagCompound checksNbt = archipelagoNbt.getCompoundTag("checks");
        checksNbt.getKeySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));


        // ************************************************************************************
        // Backwards compat
        // should make it so you can load a world from 2.2.x and it shouldn't break
        NBTTagCompound advancementChecksNbt = archipelagoNbt.getCompoundTag("advancement_checks");
        advancementChecksNbt.getKeySet().forEach(key -> checks.put(CheckType.ADVANCEMENT.addPrefix(key), advancementChecksNbt.getBoolean(key)));

        NBTTagCompound ftbQuestChecksNbt = archipelagoNbt.getCompoundTag("ftb_quest_checks");
        ftbQuestChecksNbt.getKeySet().forEach(key -> checks.put(CheckType.FTB_QUEST.addPrefix(key), ftbQuestChecksNbt.getBoolean(key)));
        // ************************************************************************************

        NBTTagCompound slotDataNbt = archipelagoNbt.getCompoundTag("slot_data");
        slotDataNbt.getKeySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        NBTTagCompound playerLastCheckDataNbt = archipelagoNbt.getCompoundTag("player_last_check");
        playerLastCheckDataNbt.getKeySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInteger(key)));

        NBTTagCompound pendingChecksNbt = archipelagoNbt.getCompoundTag("pending_checks");
        pendingChecks.addAll(pendingChecksNbt.getKeySet());
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
