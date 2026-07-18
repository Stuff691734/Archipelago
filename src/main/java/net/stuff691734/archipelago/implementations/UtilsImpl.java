package net.stuff691734.archipelago.implementations;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.mixin.DisplayInfoAccessor;
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
            id = new ResourceLocation(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.getServer().getAdvancementManager().getAdvancement(id);
        return advancement != null;
    }

    @Override
    public boolean isQuestId(String s) {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        StringTextComponent message = new StringTextComponent(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message);
            }
        });
    }

    @Override
    public void sendMessageTranslatable(String s) {
        TranslationTextComponent message = new TranslationTextComponent(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message);
            }
        });
    }

    @Override
    public void giveItem(ServerInterface serverInterface, AdvancementInterface advancementInterface, Long aLong) {
        Advancement advancement = (Advancement) advancementInterface.getAdvancement();
        if (advancement.getDisplay() != null) {
            // uses .copy() here since given items removes items from the itemstack.
            // causing advancements to not display the item stack.
            this.giveItem(serverInterface, ((DisplayInfoAccessor) advancement.getDisplay()).archipelago$getIcon().copy(), aLong);
        }
    }

    @Override
    public void giveItem(ServerInterface server, String item, Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        try {
            ItemInput itemInput = new ItemArgument().parse(new StringReader(strings[1]));
            this.giveItem(server, itemInput.createStack(amount, false), index);
        } catch (CommandSyntaxException ignored) {}
    }

    public void giveItem(ServerInterface serverInterface, ItemStack item, @Nullable Long index) {
        MinecraftServer server = (MinecraftServer) serverInterface.getServer();
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance(server) != null) {
                    if (ArchipelagoPersistentState.getInstance(server).playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0) < index) {
                        if (!player.inventory.addItemStackToInventory(item)) {
                            player.entityDropItem(item);
                        }
                    }
                }
            } else {
                if (!player.inventory.addItemStackToInventory(item)) {
                    player.entityDropItem(item);
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
