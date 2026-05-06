package net.stuff691734.archipelago.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.stuff691734.archipelago.Archipelago;

public class StartSyncChecksPacket implements IMessage {
    private String[] advancements;
    private String[] quests;

    public StartSyncChecksPacket() {
        this(new String[0], new String[0]);
    }

    public StartSyncChecksPacket(String[] advancements, String[] quests) {
        this.advancements = advancements;
        this.quests = quests;
    }

    @Override
    public void fromBytes(ByteBuf friendlyByteBuf) {
        int advancementLength = friendlyByteBuf.readInt();
        advancements = new String[advancementLength];
        for (int i = 0; i < advancementLength; i++) {
            advancements[i] = ByteBufUtils.readUTF8String(friendlyByteBuf);
        }

        int questLength = friendlyByteBuf.readInt();
        quests = new String[questLength];
        for (int i = 0; i < questLength; i++) {
            quests[i] = ByteBufUtils.readUTF8String(friendlyByteBuf);
        }
    }

    @Override
    public void toBytes(ByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(advancements.length);
        for (String advancement : advancements) {
            ByteBufUtils.writeUTF8String(friendlyByteBuf, advancement);
        }

        friendlyByteBuf.writeInt(quests.length);
        for (String quest : quests) {
            ByteBufUtils.writeUTF8String(friendlyByteBuf, quest);
        }
    }

    public static class Handler implements IMessageHandler<StartSyncChecksPacket, IMessage> {

        @Override
        public IMessage onMessage(StartSyncChecksPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Archipelago.LOGGER.info("Got archipelago check data from server.");

                Archipelago.clientState.setAdvancements(message.advancements);
                Archipelago.clientState.setQuests(message.quests);
            });
            return null;
        }
    }
}
