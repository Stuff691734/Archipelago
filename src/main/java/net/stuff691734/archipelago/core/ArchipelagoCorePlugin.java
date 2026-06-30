package net.stuff691734.archipelago.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.stuff691734.archipelago.core.FTBQuests.*;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("net.stuff691734.archipelago.core")
@IFMLLoadingPlugin.Name("Archipelago")
@IFMLLoadingPlugin.SortingIndex(10000)
public class ArchipelagoCorePlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                PlayerAdvancementsTransformer.class.getName(),
                GuiAdvancementTransformer.class.getName(),
                GuiScreenAdvancementsTransformer.class.getName(),
                DisplayInfoTransformer.class.getName(),
                JsonToNBTTransformer.class.getName(),

                ButtonQuestTransformer.class.getName(),
                ButtonRewardTransformer.class.getName(),
                MessageClaimAllRewardsTransformer.class.getName(),
                MessageClaimRewardTransformer.class.getName(),
                QuestTransformer.class.getName(),
                PanelViewQuestTransformer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}