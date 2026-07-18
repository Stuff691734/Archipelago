package net.stuff691734.archipelago.implementations;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.net.GetCheckPacket;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;
import net.stuff691734.archipelagoLib.interfaces.FTBQuestsInterface;
import net.stuff691734.archipelagoLib.interfaces.ServerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerImpl implements ServerInterface {
    private final MinecraftServer server;

    public ServerImpl(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Object getServer() {
        return this.server;
    }

    @Override
    public void execute(Runnable runnable) {
        this.server.execute(runnable);
    }

    @Override
    public void sendSlotDataPacket(Map<String, String> map) {
        this.server.getPlayerList().getPlayers().forEach(
            (player) -> ArchipelagoPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncSlotDataPacket(map)
            )
        );
    }

    @Override
    public void sendCheckPacket(String s) {
        this.server.getPlayerList().getPlayers().forEach(
            (player) -> ArchipelagoPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new GetCheckPacket(s)
            )
        );
    }

    @Override
    public void sendChecksDataPacket(List<String> list) {
        this.server.getPlayerList().getPlayers().forEach(
                (player) -> ArchipelagoPacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new StartSyncChecksPacket(list.toArray(new String[]{ }))
                )
        );
    }

    @Override
    public void setSlotData(SlotData slotData) {
        Archipelago.slotData = slotData;
    }

    @Override
    public boolean isModLoaded(String s) {
        return ModList.get().isLoaded(s);
    }

    @Override
    public AdvancementInterface getAdvancement(String s) {
        return new AdvancementImpl(this.server.getAdvancements().getAdvancement(new ResourceLocation(s)));
    }

    @Override
    public List<AdvancementInterface> getAllAdvancements() {
        return this.server.getAdvancements().getAllAdvancements().stream().map(AdvancementImpl::new).collect(Collectors.toList());
    }

    @Override
    public List<FTBQuestsInterface> getAllFTBQuests() {
        return new ArrayList<>();
    }

    @Override
    public SlotData getSlotData() {
        return Archipelago.slotData;
    }

    @Override
    public void killPlayers() {
        this.server.getPlayerList().getPlayers().forEach(
            (player) -> player.hurt(Archipelago.DeathLinkDamage, Float.MAX_VALUE)
        );
    }

    @Override
    public void updateLogic() {
        Archipelago.logic = new Logic(ArchipelagoPersistentState.getInstance(this.server), Archipelago.slotData);
    }
}
