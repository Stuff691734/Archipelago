package net.stuff691734.archipelago.events.mod;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;
import net.stuff691734.archipelago.ftbquests.implementations.FTBServerImpl;
import net.stuff691734.archipelago.ftbquests.implementations.FTBUtilsImpl;
import net.stuff691734.archipelago.implementations.ServerImpl;
import net.stuff691734.archipelago.implementations.UtilsImpl;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.archipelagoClient.ArchipelagoClient;

public class ServerStartingEvent {
    public static void onEvent(FMLServerStartingEvent event) {
        Archipelago.setServer(event.getServer());

        ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance(event.getServer());

        if (!state.slotData.isEmpty()) {
            Archipelago.slotData = new SlotData(state.slotData);
        }
        Archipelago.logic = new Logic(state, Archipelago.slotData);

        UtilsImpl utils;
        ServerImpl server;
        if (Loader.isModLoaded("ftbquests")) {
            utils = new FTBUtilsImpl();
            server = new FTBServerImpl(event.getServer());
        } else {
            utils = new UtilsImpl();
            server = new ServerImpl(event.getServer());
        }

        Archipelago.client = new ArchipelagoClient(utils, server, state);

        event.registerServerCommand(new ArchipelagoCommands());
    }
}
