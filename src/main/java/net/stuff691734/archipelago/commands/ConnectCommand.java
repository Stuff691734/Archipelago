package net.stuff691734.archipelago.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;
import net.stuff691734.archipelago.Archipelago;

import java.net.URISyntaxException;

public class ConnectCommand {
    public static int execute(CommandContext<CommandSource> context) {
        final String Name = StringArgumentType.getString(context, "Name");
        final String WebSocketAddress = StringArgumentType.getString(context, "WSAddress");
        Archipelago.client.setName(Name);
        try {
            Archipelago.client.connect(WebSocketAddress);
        } catch (URISyntaxException e) {
            context.getSource().sendFeedback(new TranslationTextComponent("archipelago.connection.invalid_server"), false);
            return 1;
        }
        return 0;
    }
}
