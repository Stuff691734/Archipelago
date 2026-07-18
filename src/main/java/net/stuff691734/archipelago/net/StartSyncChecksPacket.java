package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.stuff691734.archipelago.Archipelago;

public class StartSyncChecksPacket {
    private final String[] checks;

    public StartSyncChecksPacket(String[] checks) {
        this.checks = checks;
    }

    public static class Encoder implements MessageFunctions.MessageEncoder<StartSyncChecksPacket> {
        @Override
        public void encode(StartSyncChecksPacket message, FriendlyByteBuf buffer) {
            buffer.writeInt(message.checks.length);
            for (String check : message.checks) {
                buffer.writeUtf(check);
            }
        }
    }

    public static class Decoder implements MessageFunctions.MessageDecoder<StartSyncChecksPacket> {
        @Override
        public StartSyncChecksPacket decode(FriendlyByteBuf buffer) {
            int checksLength = buffer.readInt();
            String[] checks = new String[checksLength];
            for (int i = 0; i < checksLength; i++) {
                checks[i] = buffer.readUtf();
            }

            return new StartSyncChecksPacket(checks);
        }
    }

    public static class Handler implements MessageFunctions.MessageConsumer<StartSyncChecksPacket> {
        @Override
        public void handle(StartSyncChecksPacket packet, NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                if (FMLEnvironment.dist.isClient()) {
                    Archipelago.LOGGER.info("Got archipelago check data from server.");
                    Archipelago.CLIENT_STATE.setChecks(packet.checks);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
