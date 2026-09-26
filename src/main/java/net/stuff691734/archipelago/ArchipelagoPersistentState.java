package net.stuff691734.archipelago;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
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

    private static final Codec<ArchipelagoPersistentState> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.BOOL).fieldOf("checks").forGetter(ArchipelagoPersistentState::getChecks),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("slot_data").forGetter(ArchipelagoPersistentState::getSlotData),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("player_last_check").forGetter(ArchipelagoPersistentState::getPlayerLastCheck),
                    Codec.list(Codec.STRING).fieldOf("pending_checks").forGetter(ArchipelagoPersistentState::getPendingChecks)
            ).apply(instance, ArchipelagoPersistentState::create)
    );

    private static ArchipelagoPersistentState create(Map<String, Boolean> checks, Map<String, String> slot_data, Map<String, Integer> player_last_check, List<String> pending_checks) {
        ArchipelagoPersistentState state = new ArchipelagoPersistentState();
        state.checks = new HashMap<>(checks);
        state.slotData = new HashMap<>(slot_data);
        state.playerLastCheck = new HashMap<>(player_last_check);
        state.pendingChecks = new ArrayList<>(pending_checks);

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

    private static final SavedDataType<ArchipelagoPersistentState> type = new SavedDataType<>(
            Archipelago.MODID,
            ArchipelagoPersistentState::createNew,
            CODEC
    );

    private static ArchipelagoPersistentState getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.overworld().getDataStorage();

        ArchipelagoPersistentState state = persistentStateManager.computeIfAbsent(type);

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

    private Map<String, Integer> getPlayerLastCheck() {
        return this.playerLastCheck;
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
