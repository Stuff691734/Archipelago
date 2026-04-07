package net.stuff691734.archipelago;

import io.github.archipelagomw.flags.ItemsHandling;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;
import net.stuff691734.archipelago.events.archipealgo.ArchipelagoEvents;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
import net.stuff691734.archipelago.events.neoforge.ServerStartingEvent;
import net.stuff691734.archipelago.events.neoforge.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid=Archipelago.MODID, name="Archipelago", version="2.2.6")
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ArchipelagoClient client;
    public static MinecraftServer server;
    public static ArchipelagoPersistentState archipelagoPersistentState;
    public static SlotData slotData = new SlotData();

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").setDamageAllowedInCreativeMode();

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
}
