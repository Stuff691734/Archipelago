package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

import java.util.HashMap;
import java.util.Map;

public class SyncSlotDataPacket {
    private final Map<String, String> slotData;

    public SyncSlotDataPacket(Map<String, String> slotData) {
        this.slotData = slotData;
    }

    public static class Encoder implements MessageFunctions.MessageEncoder<SyncSlotDataPacket> {
        @Override
        public void encode(SyncSlotDataPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.slotData.get("unlock_type"));
            buffer.writeUtf(message.slotData.get("final_goal"));
            buffer.writeUtf(message.slotData.get("activated_modules"));
            buffer.writeUtf(message.slotData.get("advancement_check_difficulty"));
            buffer.writeUtf(message.slotData.get("ftb_quest_check_shape"));
            buffer.writeUtf(message.slotData.get("advancement_checks_give_items"));
            buffer.writeUtf(message.slotData.get("quest_checks_give_rewards"));
            buffer.writeUtf(message.slotData.get("death_link"));
            buffer.writeUtf(message.slotData.get("roots_unlocked"));
        }
    }

    public static class Decoder implements MessageFunctions.MessageDecoder<SyncSlotDataPacket> {
        @Override
        public SyncSlotDataPacket decode(FriendlyByteBuf buffer) {
            Map<String, String> slotData = new HashMap<>();
            slotData.put("unlock_type", buffer.readUtf());
            slotData.put("final_goal", buffer.readUtf());
            slotData.put("activated_modules", buffer.readUtf());
            slotData.put("advancement_check_difficulty", buffer.readUtf());
            slotData.put("ftb_quest_check_shape", buffer.readUtf());
            slotData.put("advancement_checks_give_items", buffer.readUtf());
            slotData.put("quest_checks_give_rewards", buffer.readUtf());
            slotData.put("death_link", buffer.readUtf());
            slotData.put("roots_unlocked", buffer.readUtf());

            return new SyncSlotDataPacket(slotData);
        }
    }

    public static class Handler implements MessageFunctions.MessageConsumer<SyncSlotDataPacket> {
        @Override
        public void handle(SyncSlotDataPacket packet, NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                if (FMLEnvironment.dist.isClient()) {
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
                }
            });
            context.setPacketHandled(true);
        }
    }
}
