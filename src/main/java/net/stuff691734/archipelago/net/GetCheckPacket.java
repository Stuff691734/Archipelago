package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.stuff691734.archipelago.Archipelago;

public record GetCheckPacket(String check) implements CustomPacketPayload {
    public static Type<GetCheckPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Archipelago.MODID, "get_check_packet"));

    public static StreamCodec<ByteBuf, GetCheckPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GetCheckPacket::check,
            GetCheckPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(GetCheckPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Archipelago.LOGGER.info("Received archipelago check from server.");
                Archipelago.CLIENT_STATE.addCheck(packet.check);
            });
        }
    }
}
