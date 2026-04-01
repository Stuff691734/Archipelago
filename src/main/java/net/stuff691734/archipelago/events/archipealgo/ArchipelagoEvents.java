package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.EventManager;

public class ArchipelagoEvents {
    public static void register(EventManager eventManager) {
        eventManager.registerListener(new ArchipelagoMessageEvent());
        eventManager.registerListener(new ConnectionEvent());
        eventManager.registerListener(new DeathLinkEvent());
        eventManager.registerListener(new ReceiveItemEvent());
    }
}
