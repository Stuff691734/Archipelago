package net.stuff691734.archipelago.events.archipealgo;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.PrintJSONEvent;
import net.minecraft.network.chat.TextComponent;
import net.stuff691734.archipelago.Utils;

public class ArchipelagoMessageEvent {
    @ArchipelagoEventListener
    public void onEvent(PrintJSONEvent event) {
        Utils.sendMessage(new TextComponent(event.apPrint.getPlainText()));
    }
}
