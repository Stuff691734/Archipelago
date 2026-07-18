package net.stuff691734.archipelago.implementations;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;
import net.stuff691734.archipelagoLib.interfaces.ServerInterface;
import net.stuff691734.archipelagoLib.interfaces.UtilsInterface;

import javax.annotation.Nullable;
import java.util.UUID;

public class UtilsImpl implements UtilsInterface {
    @Override
    public boolean isItemId(String itemId) {
        String itemName = itemId.split(" ", 2)[1];
        ItemInput itemParser = null;
        try {
            itemParser = new ItemArgument().parse(new StringReader(itemName));
        } catch (CommandSyntaxException e) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemName);
        }
        return itemParser != null;
    }

    @Override
    public boolean isAdvancementId(String advancementId) {
        if (Archipelago.getServer() == null) {
            return false;
        }
        ResourceLocation id;
        try {
            id = ResourceLocation.parse(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.getServer().getAdvancements().getAdvancement(id);
        return advancement != null;
    }

    @Override
    public boolean isQuestId(String s) {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        TextComponent message = new TextComponent(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message, UUID.randomUUID());

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message, UUID.randomUUID());
            }
        });
    }

    @Override
    public void sendMessageTranslatable(String s) {
        TranslatableComponent message = new TranslatableComponent(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message, UUID.randomUUID());

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message, UUID.randomUUID());
            }
        });
    }

    @Override
    public void giveItem(ServerInterface serverInterface, AdvancementInterface advancementInterface, Long aLong) {
        Advancement advancement = (Advancement) advancementInterface.getAdvancement();
        if (advancement.getDisplay() != null) {
            // uses .copy() here since given items removes items from the itemstack.
            // causing advancements to not display the item stack.
            this.giveItem(serverInterface, advancement.getDisplay().getIcon().copy(), aLong);
        }
    }

    @Override
    public void giveItem(ServerInterface server, String item, Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        try {
            ItemInput itemInput = new ItemArgument().parse(new StringReader(strings[1]));
            this.giveItem(server, itemInput.createItemStack(amount, false), index);
        } catch (CommandSyntaxException ignored) {}
    }

    public void giveItem(ServerInterface serverInterface, ItemStack item, @Nullable Long index) {
        MinecraftServer server = (MinecraftServer) serverInterface.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance(server) != null) {
                    if (ArchipelagoPersistentState.getInstance(server).playerLastCheck.getOrDefault(player.getStringUUID(), 0) < index) {
                        if (!player.addItem(item)) {
                            player.spawnAtLocation(item);
                        }
                    }
                }
            } else {
                if (!player.addItem(item)) {
                    player.spawnAtLocation(item);
                }
            }
        }
    }

    @Override
    public void logInfo(String s) {
        Archipelago.LOGGER.info(s);
    }

    @Override
    public void logError(String s) {
        Archipelago.LOGGER.error(s);
    }
}
