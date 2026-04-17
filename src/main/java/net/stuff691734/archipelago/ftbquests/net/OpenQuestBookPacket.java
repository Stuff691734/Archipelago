package net.stuff691734.archipelago.ftbquests.net;

import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenQuestBookPacket {
    public final Long quest_id;

    public OpenQuestBookPacket(Long id) {
        this.quest_id = id;
    }

    public void encode(PacketBuffer friendlyByteBuf) {
        friendlyByteBuf.writeLong(this.quest_id);
    }

    public OpenQuestBookPacket(PacketBuffer friendlyByteBuf) {
        this(friendlyByteBuf.readLong());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        if (ClientQuestFile.exists()) {
                            ClientQuestFile file = ClientQuestFile.INSTANCE;
                            if (file.questScreen == null) {
                                ClientQuestFile.INSTANCE.openQuestGui();
                            }

                            if (quest_id != null && file.questScreen != null) {
                                if (quest_id != 0L) {
                                    QuestObject qo = file.get(quest_id);
                                    if (qo != null) {
                                        file.questScreen.open(qo, true);
                                    }
                                } else {
                                    file.questScreen.openGui();
                                }
                            }
                        }
                    }
            );
        });
        context.get().setPacketHandled(true);
    }
}
