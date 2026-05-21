package net.stuff691734.archipelago;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.stuff691734.archipelago.events.neoforge.NeoForgeEvents;
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
    public static final ArchipelagoClientState clientState = new ArchipelagoClientState();

    public static final ResourceKey<DamageType> DeathLinkDamage = ResourceKey.create(
        Registries.DAMAGE_TYPE,
        new ResourceLocation(MODID, "death_link")
    );

    public Archipelago() {
        NeoForgeEvents.register(NeoForge.EVENT_BUS);
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
