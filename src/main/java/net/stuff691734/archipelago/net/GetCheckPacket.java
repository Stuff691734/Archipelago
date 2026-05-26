package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.stuff691734.archipelago.Archipelago;

public class GetCheckPacket {
    public String check;

    public GetCheckPacket(String check) {
        this.check = check;
    }

    public static class Encoder implements MessageFunctions.MessageEncoder<GetCheckPacket> {
        @Override
        public void encode(GetCheckPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.check);
        }
    }

    public static class Decoder implements MessageFunctions.MessageDecoder<GetCheckPacket> {
        @Override
        public GetCheckPacket decode(FriendlyByteBuf buffer) {
            String check = buffer.readUtf();

            return new GetCheckPacket(check);
        }
    }

    public static class Handler implements MessageFunctions.MessageConsumer<GetCheckPacket> {
        @Override
        public void handle(GetCheckPacket packet, NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                if (FMLEnvironment.dist.isClient()) {
                    Archipelago.LOGGER.info("Received archipelago check from server.");
                    Archipelago.clientState.addCheck(packet.check);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
