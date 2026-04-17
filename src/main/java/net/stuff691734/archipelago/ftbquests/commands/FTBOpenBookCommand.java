package net.stuff691734.archipelago.ftbquests.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.stuff691734.archipelago.ArchipelagoPacketHandler;
import net.stuff691734.archipelago.ftbquests.net.OpenQuestBookPacket;

public class FTBOpenBookCommand {
    public static int execute(CommandContext<CommandSource> context, String quest_id) throws CommandSyntaxException {
        long quest;
        try {
            quest = Long.parseLong(quest_id, 16);
        } catch (NumberFormatException ex) {
            quest = 0L;
        }
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        ArchipelagoPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new OpenQuestBookPacket(quest)
        );
        return 0;
    }
}
