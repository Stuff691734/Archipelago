package net.stuff691734.archipelago.net;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.stuff691734.archipelago.Archipelago;

import java.util.function.Supplier;

public class StartSyncChecksPacket {
    private String[] checks;

    public StartSyncChecksPacket(String[] checks) {
        this.checks = checks;
    }

    public StartSyncChecksPacket(PacketBuffer friendlyByteBuf) {
        int checksLength = friendlyByteBuf.readInt();
        checks = new String[checksLength];
        for (int i = 0; i < checksLength; i++) {
            checks[i] = friendlyByteBuf.readString();
        }
    }

    public void encode(PacketBuffer friendlyByteBuf) {
        friendlyByteBuf.writeInt(checks.length);
        for (String check : checks) {
            friendlyByteBuf.writeString(check);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.runWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        Archipelago.LOGGER.info("Got archipelago check data from server.");

                        Archipelago.clientState.addAllChecks(this.checks);
                    }
            );
        });
        context.get().setPacketHandled(true);
    }
}
