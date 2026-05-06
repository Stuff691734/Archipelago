package net.stuff691734.archipelago;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.stuff691734.archipelago.events.mod.PreInitEvent;
import net.stuff691734.archipelago.events.mod.ServerStartingEvent;
import net.stuff691734.archipelago.events.mod.ServerStoppingEvent;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mod(modid=Archipelago.MODID, name="Archipelago", version="2.2.8")
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ArchipelagoClient client;
    private static MinecraftServer server;
    public static SlotData slotData = new SlotData();
    public static final ArchipelagoClientState clientState = new ArchipelagoClientState();

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").setDamageAllowedInCreativeMode().setDamageBypassesArmor();

    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
    }

    @Mod.EventHandler
    public void onEvent(FMLServerStartingEvent event) {
        ServerStartingEvent.onEvent(event);
    }

    @Mod.EventHandler
    public void onEvent(FMLServerStoppingEvent event) {
        ServerStoppingEvent.onEvent(event);
    }

    @Mod.EventHandler
    public void onEvent(FMLPreInitializationEvent event) {
        PreInitEvent.onEvent(event);
    }


    public static @Nullable MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        Archipelago.server = server;
    }

    public static void executeOnServer(Consumer<MinecraftServer> action) {
        if (Archipelago.getServer() != null) {
            action.accept(Archipelago.getServer());
        }
    }
}
