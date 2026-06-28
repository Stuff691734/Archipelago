package net.stuff691734.archipelago;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.StringTextComponent;
import net.stuff691734.archipelago.archipelagoData.CheckType;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        if (Archipelago.getServer() == null) {
            return false;
        }
        ResourceLocation id;
        try {
            id = new ResourceLocation(advancementId);
        } catch (ResourceLocationException exception) {
            return false;
        }
        Advancement advancement = Archipelago.getServer().getAdvancements().getAdvancement(id);
        return advancement != null;
    }

    public static boolean isItemId(String itemId) {
        String itemName = itemId.split(" ", 2)[1];
        ItemInput itemParser = null;
        try {
            itemParser = new ItemArgument().parse(new StringReader(itemName));
        } catch (CommandSyntaxException e) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemName);
        }
        return itemParser != null;
    }

    public static void giveItem(ServerPlayerEntity player, ItemStack item) {
        if (!player.addItem(item)) {
            player.spawnAtLocation(item);
        }
    }

    public static void giveItem(MinecraftServer server, String item, @Nullable Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        try {
            ItemInput itemInput = new ItemArgument().parse(new StringReader(strings[1]));
            giveItem(server, itemInput.createItemStack(amount, false), index);
        } catch (CommandSyntaxException ignored) {}
    }

    public static void giveItem(MinecraftServer server, ItemStack item, @Nullable Long index) {
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            if (index != null) {
                if (ArchipelagoPersistentState.getInstance() != null) {
                    if (ArchipelagoPersistentState.getInstance().playerLastCheck.getOrDefault(player.getStringUUID(), 0) < index) {
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

    public static void sendMessage(StringTextComponent message) {
        Archipelago.executeOnServer((server) -> {
            server.sendMessage(message, UUID.randomUUID());

            for(ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                player.sendMessage(message, UUID.randomUUID());
            }
        });
    }

    public static boolean shouldAdvancementBeHidden(DisplayInfo display, Advancement advancement) {
        if (display != null) {
            if (Archipelago.slotData.roots_unlocked && Utils.getRoot(advancement) == advancement) {
                return false;
            }
            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                Advancement rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.getId().toString();

                return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(rootAdvancementName));
            }
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
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
