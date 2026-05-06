package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;

public class GetCheckPacket implements IMessage {
    public CheckType type;
    public String check;

    public GetCheckPacket() {
        this(CheckType.ADVANCEMENT, "");
    }

    public GetCheckPacket(CheckType type, String check) {
        this.type = type;
        this.check = check;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        type = CheckType.values()[friendlyByteBuf.readByte()];
        check = ByteBufUtils.readUTF8String(friendlyByteBuf);
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(type.ordinal());
        ByteBufUtils.writeUTF8String(friendlyByteBuf, check);
    }

    public static class Handler implements IMessageHandler<GetCheckPacket, IMessage> {

        @Override
        public IMessage onMessage(GetCheckPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Received archipelago check from server.");
                switch (message.type) {
                    case ADVANCEMENT:
                        Archipelago.clientState.addAdvancement(message.check);
                        break;
                    case FTB_QUESTS:
                        Archipelago.clientState.addQuest(message.check);
                        break;
                }
            });
            return null;
        }
    }

    public enum CheckType {
        ADVANCEMENT,
        FTB_QUESTS;
    }
}
