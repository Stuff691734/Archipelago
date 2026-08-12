package net.stuff691734.archipelago.implementations;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.mixin.DisplayInfoAccessor;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;
import net.stuff691734.archipelagoLib.interfaces.ServerInterface;
import net.stuff691734.archipelagoLib.interfaces.UtilsInterface;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UtilsImpl implements UtilsInterface {
    @Override
    public boolean isItemId(String itemId) {
        String item;
        try {
            item = itemId.split(" ")[1];
        } catch (IndexOutOfBoundsException exception) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemId);
            return false;
        }
        String[] itemDetails = item
                .split("\\{", 2)[0] // remove nbt
                .split(" ", 2)[0] // remove display name
                .split(":", 3);

        ResourceLocation id;
        if (itemDetails.length == 1) {
            id = new ResourceLocation(itemDetails[0]);
            return ForgeRegistries.ITEMS.containsKey(id);
        }
        else {
            id = new ResourceLocation(itemDetails[0], itemDetails[1]);
            if (ForgeRegistries.ITEMS.containsKey(id)) {
                return true;
            }
            else {
                id = new ResourceLocation(itemDetails[0]);
                return ForgeRegistries.ITEMS.containsKey(id);
            }
        }
    }

    @Override
    public boolean isAdvancementId(String advancementId) {
        if (Archipelago.getServer() == null) {
            return false;
        }
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (Exception exception) {
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
        TextComponentString message = new TextComponentString(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message);
            }
        });
    }

    @Override
    public void sendMessageTranslatable(String s) {
        TextComponentTranslation message = new TextComponentTranslation(s);
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
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

        String[] possibleNbt = strings[1].split("\\{", 2);

        NBTTagCompound nbt = null;
        if (possibleNbt.length == 2) {
            try {
                Method method = JsonToNBT.class.getMethod("archipelago$readStruct", String.class);
                nbt = (NBTTagCompound) method.invoke(JsonToNBT.class, "{" + possibleNbt[1]);
            }
            catch (NoSuchMethodException e) {
                Archipelago.LOGGER.info("Class Transformer JsonToNBTTransformer failed to add method");
            }
            catch (InvocationTargetException ignored) { /* Probably an NBTException */ }
            catch (IllegalAccessException e) {
                Archipelago.LOGGER.info("Class Transformer JsonToNBTTransformer failed to assign proper method access");
            }
        }

        // splitting on space to make sure no check display names get past here
        String[] itemDetails = possibleNbt[0].split(" ")[0].split(":", 3);


        Item item1;
        ItemStack itemStack = null;

        if (itemDetails.length == 1) {
            item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemDetails[0]));
            if (item1 != null) {
                itemStack = new ItemStack(item1, amount);
            }
        }
        if (itemDetails.length == 2) {
            item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemDetails[0], itemDetails[1]));
            if (item1 != null) {
                itemStack = new ItemStack(item1, amount);
            }
            else {
                item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemDetails[0]));
                if (item1 != null) {
                    itemStack = new ItemStack(item1, amount, Integer.parseInt(itemDetails[1]));
                }
            }
        }
        if (itemDetails.length == 3) {
            item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemDetails[0], itemDetails[1]));
            if (item1 != null) {
                itemStack = new ItemStack(item1, amount, Integer.parseInt(itemDetails[2]));
            }
        }


        if (itemStack != null) {
            if (nbt != null) {
                itemStack.setTagCompound(nbt);
            }
            giveItem(server, itemStack, index);
        }
    }

    public void giveItem(ServerInterface serverInterface, ItemStack item, @Nullable Long index) {
        MinecraftServer server = (MinecraftServer) serverInterface.getServer();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance(server) != null) {
                    if (ArchipelagoPersistentState.getInstance(server).playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0) < index) {
                        if (!player.inventory.addItemStackToInventory(item)) {
                            player.entityDropItem(item, 0);
                        }
                    }
                }
            } else {
                if (!player.inventory.addItemStackToInventory(item)) {
                    player.entityDropItem(item, 0);
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
