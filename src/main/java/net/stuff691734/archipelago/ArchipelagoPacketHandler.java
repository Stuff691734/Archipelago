package net.stuff691734.archipelago;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.stuff691734.archipelago.net.GetCheckPacket;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ArchipelagoPacketHandler {
    @SubscribeEvent
    public void init(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                StartSyncChecksPacket.TYPE,
                StartSyncChecksPacket.STREAM_CODEC,
                StartSyncChecksPacket.Handler::handle
        );

        registrar.playToClient(
                GetCheckPacket.TYPE,
                GetCheckPacket.STREAM_CODEC,
                GetCheckPacket.Handler::handle
        );

        registrar.playToClient(
                SyncSlotDataPacket.TYPE,
                SyncSlotDataPacket.STREAM_CODEC,
                SyncSlotDataPacket.Handler::handle
        );
    }
}
