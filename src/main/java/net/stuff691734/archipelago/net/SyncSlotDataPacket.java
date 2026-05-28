package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

import java.util.HashMap;
import java.util.Map;

public class SyncSlotDataPacket implements CustomPacketPayload {
    private final Map<String, String> slotData;

    public static ResourceLocation ID = new ResourceLocation(Archipelago.MODID, "sync_slot_data_packet");

    public SyncSlotDataPacket(Map<String, String> slotData) {
        this.slotData = slotData;
    }

    public SyncSlotDataPacket(FriendlyByteBuf friendlyByteBuf) {
        this.slotData = new HashMap<>();
        slotData.put("unlock_type", friendlyByteBuf.readUtf());
        slotData.put("final_goal", friendlyByteBuf.readUtf());
        slotData.put("activated_modules", friendlyByteBuf.readUtf());
        slotData.put("advancement_check_difficulty", friendlyByteBuf.readUtf());
        slotData.put("ftb_quest_check_shape", friendlyByteBuf.readUtf());
        slotData.put("advancement_checks_give_items", friendlyByteBuf.readUtf());
        slotData.put("quest_checks_give_rewards", friendlyByteBuf.readUtf());
        slotData.put("death_link", friendlyByteBuf.readUtf());
        slotData.put("roots_unlocked", friendlyByteBuf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(slotData.get("unlock_type"));
        buffer.writeUtf(slotData.get("final_goal"));
        buffer.writeUtf(slotData.get("activated_modules"));
        buffer.writeUtf(slotData.get("advancement_check_difficulty"));
        buffer.writeUtf(slotData.get("ftb_quest_check_shape"));
        buffer.writeUtf(slotData.get("advancement_checks_give_items"));
        buffer.writeUtf(slotData.get("quest_checks_give_rewards"));
        buffer.writeUtf(slotData.get("death_link"));
        buffer.writeUtf(slotData.get("roots_unlocked"));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static class Handler {
        public static void handle(SyncSlotDataPacket packet, PlayPayloadContext context) {
            context.workHandler().submitAsync(() -> {
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
            });
        }
    }
}
