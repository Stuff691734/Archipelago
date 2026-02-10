package net.stuff691734.archipelago;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;


public class ChecksState extends SavedData {
    public Map<String, Boolean> checks = new HashMap<>();
    public Map<String, String> slotData = new HashMap<>();
    public Map<String, Integer> playerLastCheck = new HashMap<>();

    private static final Codec<ChecksState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.BOOL).fieldOf("checks").forGetter(ChecksState::getChecks),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("slot_data").forGetter(ChecksState::getSlotData),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("player_last_check").forGetter(ChecksState::getPlayerLastCheck)
        ).apply(instance, ChecksState::create)
    );

    public static ChecksState create(
            Map<String, Boolean> checksMap,
                              Map<String, String> slotDataMap,
                              Map<String, Integer> playerLastCheckMap
        ) {
        ChecksState state = new ChecksState();
        // gives ImmutableMap
        state.checks = new HashMap<>(checksMap);
        state.slotData = new HashMap<>(slotDataMap);
        state.playerLastCheck = new HashMap<>(playerLastCheckMap);

        return state;
    }

    public Map<String, Boolean> getChecks() {
        return checks;
    }

    public Map<String, String> getSlotData() {
        return slotData;
    }

    public Map<String, Integer> getPlayerLastCheck() {
        return playerLastCheck;
    }

    public static ChecksState createNew() {
        ChecksState state = new ChecksState();
        state.checks = new HashMap<>();
        state.slotData = new HashMap<>();
        state.playerLastCheck = new HashMap<>();
        return state;
    }

    private static final SavedDataType<ChecksState> type = new SavedDataType<>(
            Archipelago.MODID,
            ChecksState::createNew,
            CODEC,
            null
    );

    public static ChecksState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        ChecksState state = persistentStateManager.computeIfAbsent(type);

        state.setDirty();

        return state;
    }
}
