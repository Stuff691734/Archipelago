package net.stuff691734.archipelago.events.neoforge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.ArchipelagoPersistentState;
import net.stuff691734.archipelago.commands.ArchipelagoCommands;
import net.stuff691734.archipelago.implementations.ServerImpl;
import net.stuff691734.archipelago.implementations.UtilsImpl;
import net.stuff691734.archipelagoLib.Logic;
import net.stuff691734.archipelagoLib.SlotData;
import net.stuff691734.archipelagoLib.archipelagoClient.ArchipelagoClient;

public class ServerStartingEvent {
    @SubscribeEvent
    public void onEvent(FMLServerStartingEvent event) {
        Archipelago.setServer(event.getServer());

        ArchipelagoPersistentState state = ArchipelagoPersistentState.getInstance(event.getServer());

        if (!state.slotData.isEmpty()) {
            Archipelago.slotData = new SlotData(
                    state.slotData.get("unlock_type"),
                    state.slotData.get("final_goal"),
                    state.slotData.get("activated_modules"),
                    state.slotData.get("advancement_check_difficulty"),
                    state.slotData.get("ftb_quest_check_shape"),
                    state.slotData.get("advancement_checks_give_items"),
                    state.slotData.get("quest_checks_give_rewards"),
                    state.slotData.get("death_link"),
                    state.slotData.get("roots_unlocked")
            );
        }
        Archipelago.logic = new Logic(state, Archipelago.slotData);

        UtilsImpl utils = new UtilsImpl();
        ServerImpl server = new ServerImpl(event.getServer());

        Archipelago.client = new ArchipelagoClient(utils, server, state);

        ArchipelagoCommands.register(event.getCommandDispatcher());
    }
}
