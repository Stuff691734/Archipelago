package net.stuff691734.archipelago;

import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.stuff691734.archipelago.archipelagoData.CheckType;


import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
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

    public static boolean isItemId(String itemId) {
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

    public static void giveItem(EntityPlayerMP player, ItemStack item) {
        if (!player.inventory.addItemStackToInventory(item)) {
            player.entityDropItem(item, 0);
        }
    }

    public static void giveItem(MinecraftServer server, String item, @Nullable Long index) {
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

    public static void giveItem(MinecraftServer server, ItemStack item, @Nullable Long index) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance() != null) {
                    if (ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(player.getCachedUniqueIdString(), 0) < index) {
                        giveItem(player, item);
                    }
                }
            } else {
                giveItem(player, item);
            }
        }
    }

    public static Advancement getRoot(Advancement advancement) {
        Advancement advancement1 = advancement;
        while (true) {
            Advancement advancement2 = advancement1.getParent();
            if (advancement2 == null) {
                return advancement1;
            }
            advancement1 = advancement2;
        }
    }

    public static void sendMessage(TextComponentString message) {
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message);

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message);
            }
        });
    }

    public static boolean shouldAdvancementBeHidden(DisplayInfo display, Advancement advancement) {
        if (display != null) {
            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                Advancement rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(rootAdvancementName));
            }
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
                    if (Archipelago.slotData.roots_unlocked) {
                        return false;
                    }
                    return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(advancement.getId().toString()));
                } else {
                    Advancement checkAdvancement = advancement.getParent();
                    while (checkAdvancement != null) {
                        String checkAdvancementName = checkAdvancement.getId().toString();
                        if (!ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(checkAdvancementName))) {
                            return true;
                        }
                        checkAdvancement = checkAdvancement.getParent();
                    }
                    return false;
                }
            }
            // not either, probably uninitiated
            else {
                return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(advancement.getId().toString()));
            }
        }
        return false;
    }

    public static Long getLocationId(String locationName) {
        return Archipelago.client.getDataPackage().getGame("Modded Minecraft")
                .locationNameToId.keySet()
                .stream().filter(
                    (key) -> locationName.equals(String.format("%s %s", (Object[]) key.split(" ")))
                ).findFirst().map(
                    (value) -> Archipelago.client.getDataPackage().getGame("Modded Minecraft").locationNameToId.get(value)
                ).orElse(null);
    }

    public static void sendCheck(String checkName) {
        if (Archipelago.client.isConnected()) {
            Long check_id = Utils.getLocationId(checkName);
            if (check_id != null) {
                Archipelago.client.getLocationManager().checkLocation(check_id);
                if (Archipelago.slotData.isCheckFinalGoal(checkName)) {
                    Archipelago.client.setGameState(ClientStatus.CLIENT_GOAL);
                }
            }
        } else if (ArchipelagoPersistentState.getInstance() != null) {
            ArchipelagoPersistentState.getInstance().pendingChecks.add(checkName);
            ArchipelagoPersistentState.getInstance().setDirty(true);
        }
    }
}
