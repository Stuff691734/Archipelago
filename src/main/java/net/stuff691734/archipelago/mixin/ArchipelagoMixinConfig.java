package net.stuff691734.archipelago.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ArchipelagoMixinConfig implements IMixinConfigPlugin {
    private static final Supplier<Boolean> TRUE = () -> true;
    private static final Supplier<Boolean> FTB_QUESTS_CONDITION = () -> LoadingModList.get().getModFileById("ftbquests") != null;

    private static final Map<String, Supplier<Boolean>> CONDITIONS = Map.of(
            "net.stuff691734.archipelago.mixin.FTBQuestsQuestMixin", FTB_QUESTS_CONDITION,
            "net.stuff691734.archipelago.mixin.FTBQuestsViewQuestPanelMixin", FTB_QUESTS_CONDITION,
            "net.stuff691734.archipelago.mixin.FTBQuestsTeamDataMixin", FTB_QUESTS_CONDITION
    );


    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
