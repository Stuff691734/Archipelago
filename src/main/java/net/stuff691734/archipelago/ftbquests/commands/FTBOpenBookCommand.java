package net.stuff691734.archipelago.ftbquests.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ftbquests.net.OpenQuestBookPacket;

public class FTBOpenBookCommand {
    public static int execute(CommandContext<CommandSourceStack> context, String quest_id) throws CommandSyntaxException {
        long quest;
        try {
            quest = Long.parseLong(quest_id, 16);
        } catch (NumberFormatException ex) {
            quest = 0L;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        ArchipelagoPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new OpenQuestBookPacket(quest)
        );
        return 0;
    }
}
