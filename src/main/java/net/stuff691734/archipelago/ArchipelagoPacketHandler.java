package net.stuff691734.archipelago;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.stuff691734.archipelago.ftbquests.net.OpenQuestBookPacket;

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
        INSTANCE.messageBuilder(OpenQuestBookPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenQuestBookPacket::encode)
                .decoder(OpenQuestBookPacket::new)
                .consumer(OpenQuestBookPacket::handle)
                .add();
    }
}
