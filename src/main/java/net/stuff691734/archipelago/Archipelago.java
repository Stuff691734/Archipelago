package net.stuff691734.archipelago;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Archipelago.MODID)
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ArchipelagoClient client;
    private static MinecraftServer server;
    public static SlotData slotData = new SlotData();

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").setDamageAllowedInCreativeMode().setDamageBypassesArmor();

    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
    }
}
