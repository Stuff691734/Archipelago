package net.stuff691734.archipelago;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.stuff691734.archipelago.net.GetCheckPacket;
import net.stuff691734.archipelago.net.StartSyncChecksPacket;
import net.stuff691734.archipelago.net.SyncSlotDataPacket;

public class ArchipelagoPacketHandler {
    @SubscribeEvent
    public void init(final RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(Archipelago.MODID);

        registrar.play(
                StartSyncChecksPacket.ID,
                StartSyncChecksPacket::new,
                (handler) -> handler.client(StartSyncChecksPacket.Handler::handle)
        );
        registrar.play(
                GetCheckPacket.ID,
                GetCheckPacket::new,
                (handler) -> handler.client(GetCheckPacket.Handler::handle)
        );
        registrar.play(
                SyncSlotDataPacket.ID,
                SyncSlotDataPacket::new,
                (handler) -> handler.client(SyncSlotDataPacket.Handler::handle)
        );

//        int index = 0;
//        INSTANCE.registerMessage(index++, StartSyncChecksPacket.class, StartSyncChecksPacket::encode, StartSyncChecksPacket::new, StartSyncChecksPacket::handle);
//        INSTANCE.registerMessage(index++, GetCheckPacket.class, GetCheckPacket::encode, GetCheckPacket::new, GetCheckPacket::handle);
//        INSTANCE.registerMessage(index++, SyncSlotDataPacket.class, SyncSlotDataPacket::encode, SyncSlotDataPacket::new, SyncSlotDataPacket::handle);
    }
}
