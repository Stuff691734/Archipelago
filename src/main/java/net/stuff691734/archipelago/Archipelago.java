package net.stuff691734.archipelago;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.stuff691734.archipelago.events.neoforge.ForgeEvents;
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
    public static final ArchipelagoClientState clientState = new ArchipelagoClientState();

    public static final DamageSource DeathLinkDamage = new DamageSource(MODID + ".death_link").bypassInvul().bypassArmor();


    public Archipelago() {
        ForgeEvents.register(MinecraftForge.EVENT_BUS);
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
