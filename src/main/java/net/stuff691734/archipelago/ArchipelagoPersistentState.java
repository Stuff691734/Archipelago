package net.stuff691734.archipelago;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.stuff691734.archipelago.archipelagoData.CheckType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArchipelagoPersistentState extends WorldSavedData {
    private static ArchipelagoPersistentState instance;

    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();
    public List<String> pendingChecks = new ArrayList<>();

    public ArchipelagoPersistentState(String key) {
        super(key);
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

        CompoundNBT pendingChecksNbt = new CompoundNBT();
        pendingChecks.forEach(check -> pendingChecksNbt.putString(check, check));

        archipelagoNbt.put("checks", checksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.put("pending_checks", pendingChecksNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public void read(CompoundNBT tag) {
        CompoundNBT archipelagoNbt = tag.getCompound("archipelago");

        CompoundNBT checksNbt = archipelagoNbt.getCompound("checks");
        checksNbt.keySet().forEach(key -> checks.put(key, checksNbt.getBoolean(key)));


        // ************************************************************************************
        // Backwards compat
        // should make it so you can load a world from 2.2.x and it shouldn't break
        CompoundNBT advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.keySet().forEach(key -> checks.put(CheckType.ADVANCEMENT.addPrefix(key), advancementChecksNbt.getBoolean(key)));

        CompoundNBT ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.keySet().forEach(key -> checks.put(CheckType.FTB_QUEST.addPrefix(key), ftbQuestChecksNbt.getBoolean(key)));
        // ************************************************************************************

        CompoundNBT slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.keySet().forEach(key -> slotData.put(key, slotDataNbt.getString(key)));

        CompoundNBT playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.keySet().forEach(key -> playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        CompoundNBT pendingChecksNbt = archipelagoNbt.getCompound("pending_checks");
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
        DimensionSavedDataManager persistentStateManager = server.func_71218_a(DimensionType.OVERWORLD).getSavedData();

        ArchipelagoPersistentState state = persistentStateManager.getOrCreate(
            ArchipelagoPersistentState::createNew,
            Archipelago.MODID
        );

        state.setDirty(true);

        return state;
    }

    @Nullable
    public static ArchipelagoPersistentState getInstance() {
        if (Archipelago.getServer() == null) {
            return null;
        }
        if (instance == null) {
            instance = getServerState(Archipelago.getServer());
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public static boolean getCheck(String checkName) {
        if (getInstance() != null) {
            return getInstance().checks.getOrDefault(checkName, false);
        } else {
            return Archipelago.clientState.hasCheck(checkName);
        }
    }
}
