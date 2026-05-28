package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.stuff691734.archipelago.Archipelago;

public class StartSyncChecksPacket implements CustomPacketPayload {
    private String[] checks;

    public static ResourceLocation ID = new ResourceLocation(Archipelago.MODID, "start_sync_checks_packet");

    public StartSyncChecksPacket(String[] checks) {
        this.checks = checks;
    }

    public StartSyncChecksPacket(FriendlyByteBuf friendlyByteBuf) {
        int checksLength = friendlyByteBuf.readInt();
        checks = new String[checksLength];
        for (int i = 0; i < checksLength; i++) {
            checks[i] = friendlyByteBuf.readUtf();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(checks.length);
        for (String check : checks) {
            buffer.writeUtf(check);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class Handler {
        public static void handle(StartSyncChecksPacket packet, PlayPayloadContext context) {
            context.workHandler().submitAsync(
                () -> {
                    Archipelago.LOGGER.info("Got archipelago check data from server.");
                    Archipelago.clientState.addAllChecks(packet.checks);
                }
            );
        }
    }
}
