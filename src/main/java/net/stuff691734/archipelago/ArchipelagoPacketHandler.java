package net.stuff691734.archipelago;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.stuff691734.archipelago.net.GetCheckPacket;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ArchipelagoPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Archipelago.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int index = 0;

        INSTANCE.registerMessage(index++, StartSyncChecksPacket.class, new StartSyncChecksPacket.Encoder(), new StartSyncChecksPacket.Decoder(), new StartSyncChecksPacket.Handler());
        INSTANCE.registerMessage(index++, GetCheckPacket.class, new GetCheckPacket.Encoder(), new GetCheckPacket.Decoder(), new GetCheckPacket.Handler());
        INSTANCE.registerMessage(index++, SyncSlotDataPacket.class, new SyncSlotDataPacket.Encoder(), new SyncSlotDataPacket.Decoder(), new SyncSlotDataPacket.Handler());
    }
}
