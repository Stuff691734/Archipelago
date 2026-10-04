package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;

import java.util.HashMap;
import java.util.Map;

public class SyncSlotDataPacket implements IMessage {
    private final Map<String, String> slotData;

    public SyncSlotDataPacket() {
        this(new HashMap<>());
    }

    public SyncSlotDataPacket(Map<String, String> slotData) {
        this.slotData = slotData;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        while (friendlyByteBuf.readableBytes() != 0) {
            slotData.put(ByteBufUtils.readUTF8String(friendlyByteBuf), ByteBufUtils.readUTF8String(friendlyByteBuf));
        }
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        for (Map.Entry<String, String> entry : slotData.entrySet()) {
            ByteBufUtils.writeUTF8String(friendlyByteBuf, entry.getKey());
            ByteBufUtils.writeUTF8String(friendlyByteBuf, entry.getValue());
        }
    }

    public static class Handler implements IMessageHandler<SyncSlotDataPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncSlotDataPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Got archipelago slot data from server.");
                Archipelago.slotData = new SlotData(message.slotData);
                Archipelago.logic = Archipelago.logic.updateSlotData(Archipelago.slotData);
            });
            return null;
        }
    }
}
