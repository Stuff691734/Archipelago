package net.stuff691734.archipelago;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import io.github.archipelagomw.ClientStatus;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.stuff691734.archipelago.archipelagoData.CheckType;

import javax.annotation.Nullable;
import java.util.Objects;

public class Utils {
    public static boolean isAdvancementId(String advancementId) {
        if (Archipelago.getServer() == null) {
            return false;
        }
        DataResult<ResourceLocation> id = ResourceLocation.read(advancementId);
        if (id.isSuccess()) {
            AdvancementNode advancement = Archipelago.getServer().getAdvancements().tree().get(id.getOrThrow());
            return advancement != null;
        }
        return false;
    }

    public static boolean isItemId(String itemId) {
        String itemName = itemId.split(" ", 2)[1];
        ItemParser.ItemResult itemResult = null;
        try {
            if (Archipelago.getServer() != null) {
                itemResult = new ItemParser(Archipelago.getServer().registryAccess()).parse(new StringReader(itemName));
            }
        } catch (CommandSyntaxException e) {
            Archipelago.LOGGER.error("Unable to parse item: {}", itemName);
        }
        return itemResult != null;
    }

    public static void giveItem(ServerPlayer player, ItemStack item) {
        if (!player.addItem(item)) {
            player.spawnAtLocation(item);
        }
    }

    public static void giveItem(MinecraftServer server, String item, @Nullable Long index) {
        String[] strings = item.split(" ", 2);
        int amount = Integer.parseInt(strings[0]);
        try {
            ItemParser.ItemResult itemResult = new ItemParser(server.registryAccess()).parse(new StringReader(strings[1]));
            ItemInput itemInput = new ItemInput(itemResult.item(), itemResult.components());
            giveItem(server, itemInput.createItemStack(amount, false), index);
        } catch (CommandSyntaxException ignored) {}
    }

    public static void giveItem(MinecraftServer server, ItemStack item, @Nullable Long index) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
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

    public static AdvancementNode getRoot(AdvancementNode advancement) {
        AdvancementNode advancement1 = advancement;
        while (true) {
            AdvancementNode advancement2 = advancement1.parent();
            if (advancement2 == null) {
                return advancement1;
            }
            advancement1 = advancement2;
        }
    }

    public static void sendMessage(Component message) {
        Archipelago.executeOnServer((server) -> {
            server.sendSystemMessage(message);

            for(ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(message);
            }
        });
    }

    public static boolean shouldAdvancementBeHidden(DisplayInfo display, AdvancementNode advancement) {
        if (display != null) {
            if (Archipelago.slotData.roots_unlocked && Utils.getRoot(advancement) == advancement) {
                return false;
            }
            if (Objects.equals(Archipelago.slotData.unlock_type, "tab")) {
                AdvancementNode rootAdvancement = Utils.getRoot(advancement);
                String rootAdvancementName = rootAdvancement.holder().id().toString();

                return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(rootAdvancementName));
            }
            else if (Objects.equals(Archipelago.slotData.unlock_type, "tree")) {
                if (Utils.getRoot(advancement) == advancement) {
                    return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(advancement.holder().id().toString()));
                } else {
                    AdvancementNode checkAdvancement = advancement.parent();
                    while (checkAdvancement != null) {
                        String checkAdvancementName = checkAdvancement.holder().id().toString();
                        if (!ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(checkAdvancementName))) {
                            return true;
                        }
                        checkAdvancement = checkAdvancement.parent();
                    }
                    return false;
                }
            }
            // not either, probably uninitiated
            else {
                return !ArchipelagoPersistentState.getCheck(CheckType.ADVANCEMENT.addPrefix(advancement.holder().id().toString()));
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
