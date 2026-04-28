package net.stuff691734.archipelago;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.stuff691734.archipelago.events.mod.ModEvents;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Archipelago.MODID)
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static ArchipelagoClient client;
    public static MinecraftServer server;
    public static ArchipelagoPersistentState archipelagoPersistentState;
    public static SlotData slotData = new SlotData();

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").bypassInvul().bypassArmor();

    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
        ModEvents.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
