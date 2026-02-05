package net.stuff691734.archipelago;

import com.mojang.logging.LogUtils;
import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Archipelago.MODID)
public class Archipelago {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "archipelago";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ArchipelagoClient client;
    public static MinecraftServer server;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Archipelago(IEventBus modEventBus) {

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Test) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        server = event.getServer();
        client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);
        client.getEventManager().registerListener(new ArchipelagoListeners());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        server = null;
        client.close();
        client = null;
    }

    @SubscribeEvent
    public void RegisterCommandsEvent(RegisterCommandsEvent event) {
        Commands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onEntityLoad(PlayerEvent.PlayerLoggedInEvent event) {
        int serverLastCheck = client.getItemManager().getIndex();
        int playerLastCheck = ChecksState.getServerState(server).playerLastCheck.getOrDefault(event.getEntity().getStringUUID(), 0);
        if (serverLastCheck > playerLastCheck) {
            ChecksState.getServerState(server).playerLastCheck.put(event.getEntity().getStringUUID(), serverLastCheck);

            for (NetworkItem item: client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                if (Utils.isAdvancementId(item.itemName)) {
                    ChecksState.getServerState(Archipelago.server).checks.put(item.itemName, true);
                }
                else {
                    Utils.giveItem((ServerPlayer) event.getEntity(), item.itemName);
                }
            }
        }
    }
}
