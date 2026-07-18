package net.stuff691734.archipelago.net;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelagoLib.SlotData;

import java.util.Map;

public record SyncSlotDataPacket(Map<String, String> slotData) implements CustomPacketPayload {
    public static Type<SyncSlotDataPacket> TYPE = new Type<>(new ResourceLocation(Archipelago.MODID, "sync_slot_data_packet"));

    public static StreamCodec<ByteBuf, SyncSlotDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.unboundedMap(Codec.STRING, Codec.STRING)),
            SyncSlotDataPacket::slotData,
            SyncSlotDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(SyncSlotDataPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                Archipelago.LOGGER.info("Got archipelago slot data from server.");
                Archipelago.slotData = new SlotData(
                        packet.slotData.get("unlock_type"),
                        packet.slotData.get("final_goal"),
                        packet.slotData.get("activated_modules"),
                        packet.slotData.get("advancement_check_difficulty"),
                        packet.slotData.get("ftb_quest_check_shape"),
                        packet.slotData.get("advancement_checks_give_items"),
                        packet.slotData.get("quest_checks_give_rewards"),
                        packet.slotData.get("death_link"),
                        packet.slotData.get("roots_unlocked")
                );
            });
        }
    }
}
