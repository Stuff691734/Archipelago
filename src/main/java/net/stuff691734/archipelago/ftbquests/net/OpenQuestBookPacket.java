package net.stuff691734.archipelago.ftbquests.net;

import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenQuestBookPacket implements IMessage {
    public Integer quest_id;

    public OpenQuestBookPacket() {
        this(0);
    }

    public OpenQuestBookPacket(Integer quest_id) {
        this.quest_id = quest_id;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        this.quest_id = friendlyByteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.quest_id);
    }

    public static class Handler implements IMessageHandler<OpenQuestBookPacket, IMessage> {

        @Override
        public IMessage onMessage(OpenQuestBookPacket message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (ClientQuestFile.exists()) {
                    ClientQuestFile file = ClientQuestFile.INSTANCE;
                    if (file.questTreeGui == null) {
                        ClientQuestFile.INSTANCE.openQuestGui(context.getServerHandler().player);
                    }

                    if (message.quest_id != null && file.questTreeGui != null) {
                        if (message.quest_id != 0L) {
                            QuestObject qo = file.get(message.quest_id);
                            if (qo != null) {
                                file.questTreeGui.open(qo, true);
                            }
                        } else {
                            file.questTreeGui.openGui();
                        }
                    }
                }
            });
            return null;
        }
    }
}
