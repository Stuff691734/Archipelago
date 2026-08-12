package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;

public class GetCheckPacket implements IMessage {
    public String check;

    public GetCheckPacket() {
        this("");
    }

    public GetCheckPacket(String check) {
        this.check = check;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        check = ByteBufUtils.readUTF8String(friendlyByteBuf);
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        ByteBufUtils.writeUTF8String(friendlyByteBuf, check);
    }

    public static class Handler implements IMessageHandler<GetCheckPacket, IMessage> {

        @Override
        public IMessage onMessage(GetCheckPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Received archipelago check from server.");
                Archipelago.CLIENT_STATE.addCheck(message.check);
            });
            return null;
        }
    }
}
