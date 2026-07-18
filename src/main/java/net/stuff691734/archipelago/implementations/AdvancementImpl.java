package net.stuff691734.archipelago.implementations;

import net.minecraft.advancements.AdvancementNode;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;
import net.stuff691734.archipelagoLib.interfaces.AdvancementInterface;

public class AdvancementImpl implements AdvancementInterface {
    private final AdvancementNode advancement;

    public AdvancementImpl(AdvancementNode advancement) {
        this.advancement = advancement;
    }

    @Override
    public Object getAdvancement() {
        return this.advancement;
    }

    @Override
    public String getId() {
        return this.advancement.holder().id().toString();
    }

    @Override
    public AdvancementInterface getRoot() {

        AdvancementNode advancement1 = advancement;
        while (true) {
            AdvancementNode advancement2 = advancement1.parent();
            if (advancement2 == null) {
                return new AdvancementImpl(advancement1);
            }
            advancement1 = advancement2;
        }
    }

    @Override
    public String getPage() {
        AdvancementNode advancement1 = advancement;
        while (true) {
            AdvancementNode advancement2 = advancement1.parent();
            if (advancement2 == null) {
                return advancement1.holder().id().toString();
            }
            advancement1 = advancement2;
        }
    }

    @Override
    public boolean isRoot() {
        return this.advancement.parent() == null;
    }

    @Override
    public String getName() {
        if (this.advancement.holder().value().display().isPresent()) {
            return this.advancement.holder().value().display().get().getTitle().getString();
        }
        return "";
    }

    @Override
    public AdvancementInterface getParent() {
        return new AdvancementImpl(this.advancement.parent());
    }

    @Override
    public boolean hasDisplay() {
        return this.advancement.holder().value().display().isPresent();
    }

    @Override
    public String getDifficulty() {
        if (this.advancement.holder().value().display().isPresent()) {
            return this.advancement.holder().value().display().get().getFrame().getName();
        }
        return "";
    }

    @Override
    public boolean isHidden() {
        if (this.advancement.holder().value().display().isPresent()) {
            return this.advancement.holder().value().display().get().isHidden();
        }
        return false;
    }

    @Override
    public void updateVisibility() {
        Archipelago.executeOnServer((server) -> {
            server.getPlayerList().getPlayers().forEach((player) -> {
                ((PlayerAdvancementAccessor) player.getAdvancements()).archipelago$markForVisibilityUpdate(advancement.holder());
            });
        });
    }

    @Override
    public boolean isNull() {
        return advancement == null;
    }
}
