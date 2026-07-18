package net.stuff691734.archipelago.net;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.stuff691734.archipelago.Archipelago;

import java.util.List;

public record StartSyncChecksPacket(List<String> checks) implements CustomPacketPayload {
    public static Type<StartSyncChecksPacket> TYPE = new Type<>(new ResourceLocation(Archipelago.MODID, "start_sync_checks_packet"));

    public static StreamCodec<ByteBuf, StartSyncChecksPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.list(Codec.STRING)),
            StartSyncChecksPacket::checks,
            StartSyncChecksPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(StartSyncChecksPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Archipelago.LOGGER.info("Got archipelago check data from server.");
                Archipelago.CLIENT_STATE.setChecks(packet.checks.toArray(String[]::new));
            });
        }
    }
}
