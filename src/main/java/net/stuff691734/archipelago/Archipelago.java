package net.stuff691734.archipelago;

import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Archipelago.MODID)
public class Archipelago {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "archipelago";
    // Directly reference a log4j logger
    public static Logger LOGGER = LogManager.getLogger(MODID);
    public static ArchipelagoClient client;
    public static MinecraftServer server;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Archipelago() {

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Test) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        MinecraftForge.EVENT_BUS.register(this);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Do something when the server starts
        server = event.getServer();
        client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);
        client.getEventManager().registerListener(new ArchipelagoListeners());
        Commands.register(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        server = null;
        client.close();
        client = null;
    }

    @SubscribeEvent
    public void onEntityLoad(PlayerEvent.PlayerLoggedInEvent event) {
        int serverLastCheck = client.getItemManager().getIndex();
        int playerLastCheck = ChecksState.getServerState(server).playerLastCheck.getOrDefault(event.getPlayer().getCachedUniqueIdString(), 0);
        if (serverLastCheck > playerLastCheck) {
            ChecksState.getServerState(server).playerLastCheck.put(event.getPlayer().getCachedUniqueIdString(), serverLastCheck);

            for (NetworkItem item: client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                if (Utils.isAdvancementId(item.itemName)) {
                    ChecksState.getServerState(Archipelago.server).checks.put(item.itemName, true);
                }
                else {
                    Utils.giveItem((ServerPlayerEntity) event.getPlayer(), item.itemName);
                }
            }
        }
    }
}
