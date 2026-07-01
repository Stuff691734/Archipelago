package net.stuff691734.archipelago.mixin;

import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.jarjar.metadata.json.ArtifactVersionSerializer;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ArchipelagoMixinConfig implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        if (mixinClassName.contains("FTBQuests")) {
            Optional<ModInfo> ftbQuests = LoadingModList.get().getMods().stream().filter((modInfo) -> modInfo.getModId().equals("ftbquests")).findFirst();
            if (ftbQuests.isPresent()) {
                if (
                    mixinClassName.equals("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.CollectRewardsButtonMixin") &&
                    ftbQuests.get().getVersion().compareTo(new DefaultArtifactVersion("2101.1.23")) < 0
                ) {
                    return false;
                }
                if (
                    mixinClassName.equals("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.OldCollectRewardsButtonMixin") &&
                    ftbQuests.get().getVersion().compareTo(new DefaultArtifactVersion("2101.1.23")) >= 0
                ) {
                    return false;
                }
                if (
                    mixinClassName.equals("net.stuff691734.archipelago.mixin.FTBQuests.client.gui.quests.OldQuestButtonMixin") &&
                    ftbQuests.get().getVersion().compareTo(new DefaultArtifactVersion("2101.1.20")) >= 0
                ) {
                    return false;
                }
                if (
                    mixinClassName.equals("net.stuff691734.archipelago.mixin.FTBQuests.net.ClaimAllRewardsMessageMixin") &&
                    ftbQuests.get().getVersion().compareTo(new DefaultArtifactVersion("2101.1.24")) >= 0
                ) {
                    return false;
                }
                if (
                        mixinClassName.equals("net.stuff691734.archipelago.mixin.FTBQuests.net.NewClaimAllRewardsMessageMixin") &&
                                ftbQuests.get().getVersion().compareTo(new DefaultArtifactVersion("2101.1.24")) < 0
                ) {
                    return false;
                }

                return true;
            }
        }

        return true;
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
