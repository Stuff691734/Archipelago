package net.stuff691734.archipelago;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.stuff691734.archipelago.events.mod.ModEvents;
import net.stuff691734.archipelago.events.neoforge.NeoForgeEvents;
import net.stuff691734.archipelagoLib.ArchipelagoClientState;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.archipelagoClient.ArchipelagoClient;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mod(Archipelago.MODID)
public class Archipelago {
    public static final String MODID = "archipelago";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ArchipelagoClient client;
    private static MinecraftServer server;
    public static SlotData slotData = new SlotData();
    public static final ArchipelagoClientState CLIENT_STATE = new ArchipelagoClientState();
    public static Logic logic = new Logic(Archipelago.CLIENT_STATE, Archipelago.slotData);

    public static final ResourceKey<DamageType> DeathLinkDamage = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        new ResourceLocation(MODID, "death_link")
    );

    public Archipelago(IEventBus eventBus) {
        NeoForgeEvents.register(NeoForge.EVENT_BUS);
        ModEvents.register(eventBus);
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
