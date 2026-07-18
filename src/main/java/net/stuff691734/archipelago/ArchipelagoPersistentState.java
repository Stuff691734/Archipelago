package net.stuff691734.archipelago;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.stuff691734.archipelagoLib.CheckType;
import net.stuff691734.archipelagoLib.interfaces.ServerStorageInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArchipelagoPersistentState extends SavedData implements ServerStorageInterface {
    private static ArchipelagoPersistentState instance;

    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();
    public List<String> pendingChecks = new ArrayList<>();


    @Override
    public CompoundTag save(CompoundTag nbt) {
        CompoundTag archipelagoNbt = new CompoundTag();

        CompoundTag checksNbt = new CompoundTag();
        checks.forEach(checksNbt::putBoolean);

        CompoundTag slotDataNbt = new CompoundTag();
        slotData.forEach(slotDataNbt::putString);

        CompoundTag playerLastCheckDataNbt = new CompoundTag();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        CompoundTag pendingChecksNbt = new CompoundTag();
        pendingChecks.forEach(check -> pendingChecksNbt.putString(check, check));

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.put("pending_checks", pendingChecksNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public static ArchipelagoPersistentState createFromNbt(CompoundTag tag) {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState();
        CompoundTag archipelagoNbt = tag.getCompound("archipelago");

        CompoundTag checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.getAllKeys().forEach(key -> state.checks.put(key, checksNbt.getBoolean(key)));


        // ************************************************************************************
        // Backwards compat
        // should make it so you can load a world from 2.2.x and it shouldn't break
        CompoundTag advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.getAllKeys().forEach(key -> state.checks.put(CheckType.ADVANCEMENT.addPrefix(key), advancementChecksNbt.getBoolean(key)));

        CompoundTag ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.getAllKeys().forEach(key -> state.checks.put(CheckType.FTB_QUEST.addPrefix(key), ftbQuestChecksNbt.getBoolean(key)));
        // ************************************************************************************

        CompoundTag slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getAllKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        CompoundTag playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getAllKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        CompoundTag pendingChecksNbt = archipelagoNbt.getCompound("pending_checks");
        state.pendingChecks.addAll(pendingChecksNbt.getAllKeys());

        return state;
    }

    public static ArchipelagoPersistentState createNew() {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState();
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        state.pendingChecks = new ArrayList<>();
        return state;
    }

    private static ArchipelagoPersistentState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        ArchipelagoPersistentState state = persistentStateManager.computeIfAbsent(
            ArchipelagoPersistentState::createFromNbt,
            ArchipelagoPersistentState::createNew,
            Archipelago.MODID
        );

        state.setDirty();

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
                if (this.playerLastCheck.getOrDefault(player.getStringUUID(), 0) < aLong) {
                    this.playerLastCheck.put(player.getStringUUID(), aLong.intValue());
                }
            });
        });
    }
}
