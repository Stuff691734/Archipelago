package net.stuff691734.archipelago.implementations;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
import java.util.Optional;

public class UtilsImpl implements UtilsInterface {
    @Override
    public boolean isItemId(String itemId) {
        String itemName = itemId.split(" ", 2)[1];
        ItemParser.ItemResult itemParser = null;
        try {
            if (Archipelago.getServer() != null) {
                itemParser = new ItemParser(Archipelago.getServer().registryAccess()).parse(new StringReader(itemName));
            }
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
        AdvancementNode advancement = Archipelago.getServer().getAdvancements().tree().get(id);
        return advancement != null;
    }

    @Override
    public boolean isQuestId(String s) {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        Component message = Component.literal(s);
        Archipelago.executeOnServer((server) -> {
            server.sendSystemMessage(message);

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(message);
            }
        });
    }

    @Override
    public void sendMessageTranslatable(String s) {
        Component message = Component.translatable(s);
        Archipelago.executeOnServer((server) -> {
            server.sendSystemMessage(message);

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(message);
            }
        });
    }

    @Override
    public void giveItem(ServerInterface serverInterface, AdvancementInterface advancementInterface, Long aLong) {
        AdvancementNode advancement = (AdvancementNode) advancementInterface.getAdvancement();
        if (advancement.holder().value().display().isPresent()) {
            this.giveItem(serverInterface, advancement.holder().value().display().get().getIcon(), aLong);
        }
    }

    @Override
    public void giveItem(ServerInterface server, String item, Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        try {
            ItemParser.ItemResult itemResult = new ItemParser(((MinecraftServer) server.getServer()).registryAccess()).parse(new StringReader(strings[1]));
            ItemInput itemInput = new ItemInput(itemResult.item(), itemResult.components());
            this.giveItem(server, itemInput.createItemStack(amount, false), index);
        } catch (CommandSyntaxException ignored) {}
    }

    public void giveItem(ServerInterface serverInterface, ItemStack item, @Nullable Long index) {
        MinecraftServer server = (MinecraftServer) serverInterface.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack playerItem = item.copy();
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance(server) != null) {
                    if (ArchipelagoPersistentState.getInstance(server).playerLastCheck.getOrDefault(player.getStringUUID(), 0) < index) {
                        if (!player.addItem(playerItem.copy())) {
                            player.spawnAtLocation(playerItem);
                        }
                    }
                }
            } else {
                if (!player.addItem(playerItem)) {
                    player.spawnAtLocation(playerItem);
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
