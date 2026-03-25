package net.stuff691734.archipelago;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
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

    public static final ResourceKey<DamageType> DeathLinkDamage = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        new ResourceLocation(MODID, "death_link")
    );

    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
    }
}
