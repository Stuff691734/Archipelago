package net.stuff691734.archipelago;

import io.github.archipelagomw.Client;
import net.minecraft.util.text.StringTextComponent;

public class ArchipelagoClient extends Client {
    @Override
    public void onError(Exception ex) {
        Archipelago.LOGGER.info(ex.getLocalizedMessage());
        Utils.sendMessage(new StringTextComponent(ex.getLocalizedMessage()));
    }

    @Override
    public void onClose(String Reason, int attemptingReconnect) {
        Archipelago.LOGGER.info(Reason);
    }
}
