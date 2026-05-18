package net.stuff691734.archipelago.net;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.SlotData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncSlotDataPacket {
    private final Map<String, String> slotData;

    public SyncSlotDataPacket(Map<String, String> slotData) {
        this.slotData = slotData;
    }

    public SyncSlotDataPacket(PacketBuffer friendlyByteBuf) {
        this.slotData = new HashMap<>();
        slotData.put("unlock_type", friendlyByteBuf.readString());
        slotData.put("final_goal", friendlyByteBuf.readString());
        slotData.put("activated_modules", friendlyByteBuf.readString());
        slotData.put("advancement_check_difficulty", friendlyByteBuf.readString());
        slotData.put("ftb_quest_check_shape", friendlyByteBuf.readString());
        slotData.put("advancement_checks_give_items", friendlyByteBuf.readString());
        slotData.put("quest_checks_give_rewards", friendlyByteBuf.readString());
        slotData.put("death_link", friendlyByteBuf.readString());
        slotData.put("roots_unlocked", friendlyByteBuf.readString());
    }

    public void encode(PacketBuffer friendlyByteBuf) {
        friendlyByteBuf.writeString(slotData.get("unlock_type"));
        friendlyByteBuf.writeString(slotData.get("final_goal"));
        friendlyByteBuf.writeString(slotData.get("activated_modules"));
        friendlyByteBuf.writeString(slotData.get("advancement_check_difficulty"));
        friendlyByteBuf.writeString(slotData.get("ftb_quest_check_shape"));
        friendlyByteBuf.writeString(slotData.get("advancement_checks_give_items"));
        friendlyByteBuf.writeString(slotData.get("quest_checks_give_rewards"));
        friendlyByteBuf.writeString(slotData.get("death_link"));
        friendlyByteBuf.writeString(slotData.get("roots_unlocked"));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.runWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        Archipelago.LOGGER.info("Got archipelago slot data from server.");
                        Archipelago.slotData = new SlotData(
                                this.slotData.get("unlock_type"),
                                this.slotData.get("final_goal"),
                                this.slotData.get("activated_modules"),
                                this.slotData.get("advancement_check_difficulty"),
                                this.slotData.get("ftb_quest_check_shape"),
                                this.slotData.get("advancement_checks_give_items"),
                                this.slotData.get("quest_checks_give_rewards"),
                                this.slotData.get("death_link"),
                                this.slotData.get("roots_unlocked")
                        );
                    }
            );
        });
        context.get().setPacketHandled(true);
    }
}
