package net.stuff691734.archipelago.ftbquests.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ftbquests.net.OpenQuestBookPacket;

public class FTBOpenBookCommand {
    public static void execute(ICommandSender sender, String quest_id) {
        int quest;
        try {
            quest = Integer.parseUnsignedInt(quest_id, 16);
        } catch (NumberFormatException ex) {
            quest = 0;
        }
        Archipelago.LOGGER.info(quest);
        Archipelago.LOGGER.info(quest_id);
        EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
        ArchipelagoPacketHandler.INSTANCE.sendTo(
                new OpenQuestBookPacket(quest),
                player
        );
    }
}
