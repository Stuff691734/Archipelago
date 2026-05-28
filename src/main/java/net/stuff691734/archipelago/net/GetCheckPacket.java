package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.stuff691734.archipelago.Archipelago;

public class GetCheckPacket implements CustomPacketPayload {
    public String check;

    public static ResourceLocation ID = new ResourceLocation(Archipelago.MODID, "get_check_packet");

    public GetCheckPacket(String check) {
        this.check = check;
    }

    public GetCheckPacket(FriendlyByteBuf friendlyByteBuf) {
        this.check = friendlyByteBuf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(check);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class Handler {
        public static void handle(GetCheckPacket packet, PlayPayloadContext context) {
            context.workHandler().submitAsync(() -> {
                Archipelago.LOGGER.info("Received archipelago check from server.");
                Archipelago.clientState.addCheck(packet.check);
            });
        }
    }
}
