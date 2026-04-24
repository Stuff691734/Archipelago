package net.stuff691734.archipelago;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.stuff691734.archipelago.ftbquests.net.OpenQuestBookPacket;

public class ArchipelagoPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Archipelago.MODID + ":main");

    public static void init() {
        int index = 0;
        INSTANCE.registerMessage(OpenQuestBookPacket.Handler.class, OpenQuestBookPacket.class, index++, Side.CLIENT);
    }
}
