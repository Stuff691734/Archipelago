package net.stuff691734.archipelago;

import io.github.archipelagomw.flags.ItemsHandling;
import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.stuff691734.archipelago.commands.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid=Archipelago.MODID, name="Archipelago", version="1.0")
public class Archipelago {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "archipelago";
    // Directly reference a log4j logger
    public static final Logger LOGGER = LogManager.getLogger(MODID);
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
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        // Do something when the server starts
        server = event.getServer();
        client = new ArchipelagoClient();

        client.setGame("Modded Minecraft");

        client.setItemsHandlingFlags(ItemsHandling.SEND_STARTING_INVENTORY | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_ITEMS);
        client.getEventManager().registerListener(new ArchipelagoListeners());
        event.registerServerCommand(new Commands());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        server = null;
        client.close();
        client = null;
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            if (event.getSource().canHarmInCreative()) {
                // no looping hopefully
                Archipelago.client.sendDeathlink(
                        Archipelago.client.getMyName(),
                        event.getSource().getDeathMessage(event.getEntityLiving()).getFormattedText()
                );
            }
        }
    }


    @SubscribeEvent
    public void onEntityLoad(PlayerEvent.PlayerLoggedInEvent event) {
        int serverLastCheck = client.getItemManager().getIndex();
        ChecksState checksState = ChecksState.getServerState(server);
        if (checksState != null) {
            int playerLastCheck = checksState.playerLastCheck.getOrDefault(event.player.getCachedUniqueIdString(), 0);
            if (serverLastCheck > playerLastCheck) {
                checksState.playerLastCheck.put(event.player.getCachedUniqueIdString(), serverLastCheck);

                for (NetworkItem item: client.getItemManager().getReceivedItems().subList(playerLastCheck, serverLastCheck)) {
                    if (Utils.isAdvancementId(item.itemName)) {
                        checksState.checks.put(item.itemName, true);
                    }
                    else {
                        Utils.giveItem((EntityPlayerMP) event.player, item.itemName);
                    }
                }
            }
        }
    }
}
