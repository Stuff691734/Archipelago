package net.stuff691734.archipelago;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.stuff691734.archipelago.events.mod.ModEvents;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
import net.stuff691734.archipelagoLib.ArchipelagoClientState;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.archipelagoClient.ArchipelagoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mod(Archipelago.MODID)
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static ArchipelagoClient client;
    private static MinecraftServer server;
    public static SlotData slotData = new SlotData();
    public static final ArchipelagoClientState CLIENT_STATE = new ArchipelagoClientState();
    public static Logic logic = new Logic(Archipelago.CLIENT_STATE, Archipelago.slotData);

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").bypassInvul().bypassArmor();


    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
        ModEvents.register(FMLJavaModLoadingContext.get().getModEventBus());
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
