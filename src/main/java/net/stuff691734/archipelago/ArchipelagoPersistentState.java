package net.stuff691734.archipelago;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArchipelagoPersistentState extends WorldSavedData {
    public Map<String, Boolean> advancementChecks = new HashMap<>();
    public Map<String, Boolean> ftbQuestChecks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();
    public List<String> pendingChecks = new ArrayList<>();

    public ArchipelagoPersistentState(String key) {
        super(key);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        CompoundNBT archipelagoNbt = new CompoundNBT();

        CompoundNBT advancementChecksNbt = new CompoundNBT();
        advancementChecks.forEach(advancementChecksNbt::putBoolean);

        CompoundNBT ftbQuestChecksNbt = new CompoundNBT();
        ftbQuestChecks.forEach(ftbQuestChecksNbt::putBoolean);

        CompoundNBT slotDataNbt = new CompoundNBT();
        slotData.forEach(slotDataNbt::putString);

        CompoundNBT playerLastCheckDataNbt = new CompoundNBT();
        playerLastCheck.forEach(playerLastCheckDataNbt::putInt);

        CompoundNBT pendingChecksNbt = new CompoundNBT();
        pendingChecks.forEach(check -> pendingChecksNbt.putString(check, check));

        archipelagoNbt.put("advancement_checks", advancementChecksNbt);
        archipelagoNbt.put("ftb_quest_checks", ftbQuestChecksNbt);
        archipelagoNbt.put("slot_data", slotDataNbt);
        archipelagoNbt.put("player_last_check", playerLastCheckDataNbt);
        archipelagoNbt.put("pending_checks", pendingChecksNbt);

        nbt.put("archipelago", archipelagoNbt);

        return nbt;
    }

    public void load(CompoundNBT tag) {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState(Archipelago.MODID);
        CompoundNBT archipelagoNbt = tag.getCompound("archipelago");

        CompoundNBT advancementChecksNbt = archipelagoNbt.getCompound("advancement_checks");
        advancementChecksNbt.getAllKeys().forEach(key -> state.advancementChecks.put(key, advancementChecksNbt.getBoolean(key)));

        CompoundNBT ftbQuestChecksNbt = archipelagoNbt.getCompound("ftb_quest_checks");
        ftbQuestChecksNbt.getAllKeys().forEach(key -> state.ftbQuestChecks.put(key, ftbQuestChecksNbt.getBoolean(key)));

        CompoundNBT slotDataNbt = archipelagoNbt.getCompound("slot_data");
        slotDataNbt.getAllKeys().forEach(key -> state.slotData.put(key, slotDataNbt.getString(key)));

        CompoundNBT playerLastCheckDataNbt = archipelagoNbt.getCompound("player_last_check");
        playerLastCheckDataNbt.getAllKeys().forEach(key -> state.playerLastCheck.put(key, playerLastCheckDataNbt.getInt(key)));

        CompoundNBT pendingChecksNbt = archipelagoNbt.getCompound("pending_checks");
        state.pendingChecks.addAll(pendingChecksNbt.getAllKeys());
    }

    public static ArchipelagoPersistentState createNew() {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState(Archipelago.MODID);
        state.advancementChecks = new HashMap<>();
        state.ftbQuestChecks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        state.pendingChecks = new ArrayList<>();
        return state;
    }

    public static ArchipelagoPersistentState getServerState(MinecraftServer server) {
        DimensionSavedDataManager persistentStateManager = server.overworld().getDataStorage();

        ArchipelagoPersistentState state = persistentStateManager.computeIfAbsent(
            ArchipelagoPersistentState::createNew,
            Archipelago.MODID
        );

        state.setDirty();

        return state;
    }
}
