package net.stuff691734.archipelago.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
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

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(slotData.get("unlock_type"));
        friendlyByteBuf.writeUtf(slotData.get("final_goal"));
        friendlyByteBuf.writeUtf(slotData.get("activated_modules"));
        friendlyByteBuf.writeUtf(slotData.get("advancement_check_difficulty"));
        friendlyByteBuf.writeUtf(slotData.get("ftb_quest_check_shape"));
        friendlyByteBuf.writeUtf(slotData.get("advancement_checks_give_items"));
        friendlyByteBuf.writeUtf(slotData.get("quest_checks_give_rewards"));
        friendlyByteBuf.writeUtf(slotData.get("death_link"));
        friendlyByteBuf.writeUtf(slotData.get("roots_unlocked"));
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(
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
