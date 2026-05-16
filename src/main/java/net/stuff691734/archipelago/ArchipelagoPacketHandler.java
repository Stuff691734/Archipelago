package net.stuff691734.archipelago;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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

        INSTANCE.registerMessage(index++, StartSyncChecksPacket.class, StartSyncChecksPacket::encode, StartSyncChecksPacket::new, StartSyncChecksPacket::handle);
        INSTANCE.registerMessage(index++, GetCheckPacket.class, GetCheckPacket::encode, GetCheckPacket::new, GetCheckPacket::handle);
        INSTANCE.registerMessage(index++, SyncSlotDataPacket.class, SyncSlotDataPacket::encode, SyncSlotDataPacket::new, SyncSlotDataPacket::handle);
    }
}
