package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.stuff691734.archipelago.Archipelago;

import java.util.function.Supplier;

public class GetCheckPacket {
    public String check;

    public GetCheckPacket(String check) {
        this.check = check;
    }

    public GetCheckPacket(FriendlyByteBuf friendlyByteBuf) {
        this.check = friendlyByteBuf.readUtf();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(check);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        Archipelago.LOGGER.info("Received archipelago check from server.");
                        Archipelago.CLIENT_STATE.addCheck(this.check);
                    }
            );
        });
        context.get().setPacketHandled(true);
    }
}
