package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.ModList;
import net.stuff691734.archipelago.ftbquests.commands.FTBOpenBookCommand;

public class OpenBookCommand {
    public static int execute(CommandContext<CommandSource> context, String quest_id) throws CommandSyntaxException {
        if (ModList.get().isLoaded("ftbquests")) {
            return FTBOpenBookCommand.execute(context, quest_id);
        } else {
            context.getSource().sendFeedback(new StringTextComponent("FTBQuests not installed"), false);
            return 1;
        }
    }
}
