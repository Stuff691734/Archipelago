package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;
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
        slotData.put("unlock_type", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("final_goal", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("activated_modules", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("advancement_check_difficulty", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("ftb_quest_check_shape", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("advancement_checks_give_items", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("quest_checks_give_rewards", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("death_link", ByteBufUtils.readUTF8String(friendlyByteBuf));
        slotData.put("roots_unlocked", ByteBufUtils.readUTF8String(friendlyByteBuf));
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("unlock_type"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("final_goal"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("activated_modules"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("advancement_check_difficulty"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("ftb_quest_check_shape"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("advancement_checks_give_items"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("quest_checks_give_rewards"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("death_link"));
        ByteBufUtils.writeUTF8String(friendlyByteBuf, slotData.get("roots_unlocked"));
    }

    public static class Handler implements IMessageHandler<SyncSlotDataPacket, IMessage> {

        @Override
        public IMessage onMessage(SyncSlotDataPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Got archipelago slot data from server.");
                Archipelago.slotData = new SlotData(
                        message.slotData.get("unlock_type"),
                        message.slotData.get("final_goal"),
                        message.slotData.get("activated_modules"),
                        message.slotData.get("advancement_check_difficulty"),
                        message.slotData.get("ftb_quest_check_shape"),
                        message.slotData.get("advancement_checks_give_items"),
                        message.slotData.get("quest_checks_give_rewards"),
                        message.slotData.get("death_link"),
                        message.slotData.get("roots_unlocked")
                );
            });
            return null;
        }
    }
}
