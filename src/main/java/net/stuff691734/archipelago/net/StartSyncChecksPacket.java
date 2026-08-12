package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;

public class StartSyncChecksPacket implements IMessage {
    private String[] checks;

    public StartSyncChecksPacket() {
        this(new String[0]);
    }

    public StartSyncChecksPacket(String[] checks) {
        this.checks = checks;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        int checksLength = friendlyByteBuf.readInt();
        checks = new String[checksLength];
        for (int i = 0; i < checksLength; i++) {
            checks[i] = ByteBufUtils.readUTF8String(friendlyByteBuf);
        }
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(checks.length);
        for (String check : checks) {
            ByteBufUtils.writeUTF8String(friendlyByteBuf, check);
        }
    }

    public static class Handler implements IMessageHandler<StartSyncChecksPacket, IMessage> {

        @Override
        public IMessage onMessage(StartSyncChecksPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Got archipelago check data from server.");

                Archipelago.CLIENT_STATE.setChecks(message.checks);
            });
            return null;
        }
    }
}
