package net.stuff691734.archipelago;

import io.github.archipelagomw.Client;
import net.minecraft.network.chat.Component;

public class ArchipelagoClient extends Client {
    @Override
    public void onError(Exception ex) {
        Archipelago.LOGGER.info(ex.getLocalizedMessage());
        Utils.sendMessage(Component.literal(ex.getLocalizedMessage()));
    }

    @Override
    public void onClose(String Reason, int attemptingReconnect) {
        Archipelago.LOGGER.info(Reason);
    }
}
