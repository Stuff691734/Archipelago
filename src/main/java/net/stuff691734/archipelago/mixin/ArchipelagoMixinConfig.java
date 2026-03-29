package net.stuff691734.archipelago.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArchipelagoMixinConfig implements IMixinConfigPlugin {
    private static final Supplier<Boolean> TRUE = () -> true;
    private static final Supplier<Boolean> FTB_QUESTS_CONDITION = () -> LoadingModList.get().getModFileById("ftbquests") != null;
    private static final BiFunction<String, String, Boolean> EQUAL_OR_OVER_VERSION_CONDITION = (modName, version) -> compareVersions(LoadingModList.get().getModFileById(modName).versionString(), version);
    private static final BiFunction<String, String, Boolean> UNDER_VERSION_CONDITION = (modName, version) -> !compareVersions(LoadingModList.get().getModFileById(modName).versionString(), version);
    private static final Function<String, Boolean> FTB_QUESTS_EQUAL_OR_OVER_VERSION_CONDITION = (version) -> FTB_QUESTS_CONDITION.get() && EQUAL_OR_OVER_VERSION_CONDITION.apply("ftbquests", version);
    private static final Function<String, Boolean> FTB_QUESTS_UNDER_VERSION_CONDITION = (version) -> FTB_QUESTS_CONDITION.get() && UNDER_VERSION_CONDITION.apply("ftbquests", version);

    private static boolean compareVersions(String version1, String version2) {
        // returns if the first item is bigger or equal
        String[] version1Split = version1.split("\\.");
        String[] version2Split = version2.split("\\.");
        if (version1Split.length != version2Split.length) {
            throw new RuntimeException("could not compare versions {} and {}");
        }
        for (int i = 0; i < version1Split.length; i++) {
            int result = Integer.compare(Integer.parseInt(version1Split[i]), Integer.parseInt(version2Split[i]));
            if (result != 0) {
                return result > 0;
            }
        }
        return true;
    }

    private static final Map<String, Supplier<Boolean>> CONDITIONS = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.CollectRewardsButtonMixin", () -> FTB_QUESTS_EQUAL_OR_OVER_VERSION_CONDITION.apply("2101.1.23")),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.OldCollectRewardsButtonMixin", () -> FTB_QUESTS_UNDER_VERSION_CONDITION.apply("2101.1.23")),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.QuestButtonMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.OldQuestButtonMixin", () -> FTB_QUESTS_UNDER_VERSION_CONDITION.apply("2101.1.20")),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.RewardButtonMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.ViewQuestPanelMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.net.ClaimAllRewardsMessageMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.net.ClaimRewardMessageMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.quest.task.AdvancementTaskMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.quest.QuestMixin", FTB_QUESTS_CONDITION),
            new AbstractMap.SimpleEntry<String, Supplier<Boolean>>("net.stuff691734.archipelago.mixin.FTBQuests.quest.TeamDataMixin", FTB_QUESTS_CONDITION)
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
