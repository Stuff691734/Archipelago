package net.stuff691734.archipelago;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.stuff691734.archipelago.net.GetCheckPacket;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ArchipelagoPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Archipelago.MODID + ":main");

    public static void init() {
        int index = 0;
        INSTANCE.registerMessage(StartSyncChecksPacket.Handler.class, StartSyncChecksPacket.class, index++, Side.CLIENT);
        INSTANCE.registerMessage(GetCheckPacket.Handler.class, GetCheckPacket.class, index++, Side.CLIENT);
        INSTANCE.registerMessage(SyncSlotDataPacket.Handler.class, SyncSlotDataPacket.class, index++, Side.CLIENT);
    }
}
