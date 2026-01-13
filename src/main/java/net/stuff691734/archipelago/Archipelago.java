package net.stuff691734.archipelago;

import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.parts.NetworkItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Archipelago implements ModInitializer {
    public static final String MOD_ID = "archipelago";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ArchipelagoClient client;
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        Commands.register();

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            server = minecraftServer;
            client = new ArchipelagoClient();

            client.setGame("Modded Minecraft");

            client.addTag("Modded Minecraft");
            client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);
            client.getEventManager().registerListener(new ArchipelagoListeners());
        });


        ServerLivingEntityEvents.AFTER_DEATH.register(((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity) {
                if (damageSource == entity.getDamageSources().genericKill()) {
                    // no looping hopefully

                    Archipelago.client.sendDeathlink(
                            Archipelago.client.getMyName(),
                            damageSource.getDeathMessage(entity).getLiteralString()
                    );
                }
            }
        }));

        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) entity;
                int serverLastCheck = client.getItemManager().getIndex();
                int playerLastCheck = ChecksState.getServerState(server).playerLastCheck.getOrDefault(player.getUuidAsString(), 0);
                if (serverLastCheck != playerLastCheck) {
                    ChecksState.getServerState(server).playerLastCheck.put(player.getUuidAsString(), serverLastCheck);

                    for (NetworkItem item: client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                        if (Utils.isRootAdvancementId(item.itemName)) {
                            ChecksState.getServerState(Archipelago.server).checks.put(item.itemName, true);
                        }
                        else {
                            Utils.giveItem(player, item.itemName);
                        }
                    }
                }
            }
        });

        // close websocket when leaving
        ServerLifecycleEvents.SERVER_STOPPING.register((minecraftServer) -> {
            server = null;
            client.close();
            client = null;
        });
    }
}
