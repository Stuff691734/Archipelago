package net.stuff691734.archipelago.implementations;

import net.minecraft.advancements.Advancement;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;

public class AdvancementImpl implements AdvancementInterface {
    private final Advancement advancement;

    public AdvancementImpl(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Object getAdvancement() {
        return this.advancement;
    }

    @Override
    public String getId() {
        return this.advancement.getId().toString();
    }

    @Override
    public AdvancementInterface getRoot() {
        Advancement advancement1 = advancement;
        while (true) {
            Advancement advancement2 = advancement1.getParent();
            if (advancement2 == null) {
                return new AdvancementImpl(advancement1);
            }
            advancement1 = advancement2;
        }
    }

    @Override
    public String getPage() {
        Advancement advancement1 = advancement;
        while (true) {
            Advancement advancement2 = advancement1.getParent();
            if (advancement2 == null) {
                return advancement1.getId().toString();
            }
            advancement1 = advancement2;
        }
    }

    @Override
    public boolean isRoot() {
        return this.advancement.getParent() == null;
    }

    @Override
    public String getName() {
        if (this.advancement.getDisplay() != null) {
            return this.advancement.getDisplay().getTitle().getString();
        }
        return "";
    }

    @Override
    public AdvancementInterface getParent() {
        return new AdvancementImpl(this.advancement.getParent());
    }

    @Override
    public boolean hasDisplay() {
        return this.advancement.getDisplay() != null;
    }

    @Override
    public String getDifficulty() {
        if (this.advancement.getDisplay() != null) {
            return this.advancement.getDisplay().getFrame().getName();
        }
        return "";
    }

    @Override
    public boolean isHidden() {
        if (this.advancement.getDisplay() != null) {
            return this.advancement.getDisplay().isHidden();
        }
        return false;
    }

    @Override
    public void updateVisibility() {
        Archipelago.executeOnServer((server) -> {
            server.getPlayerList().getPlayers().forEach((player) -> {
                ((PlayerAdvancementAccessor) player.getAdvancements()).archipelago$ensureVisibility(advancement);
            });
        });
    }

    @Override
    public boolean isNull() {
        return advancement == null;
    }
}
