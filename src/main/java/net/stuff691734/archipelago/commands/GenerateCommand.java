package net.stuff691734.archipelago.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.stuff691734.archipelago.Archipelago;
import net.stuff691734.archipelago.archipelagoData.AdvancementsCheck;
import net.stuff691734.archipelago.archipelagoData.Check;
import net.stuff691734.archipelago.ftbquests.commands.FTBGenerateCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateCommand {
    public static int execute(CommandContext<CommandSourceStack> context, boolean singleLine, boolean removePermaHidden) {
        context.getSource().sendSuccess(Component.literal("Started writing to file."), false);

        Map<String, Map<String, ? extends Check>> checks = new HashMap<>();

        if (ModList.get().isLoaded("ftbquests")) {
            checks.put("FTBQuests", FTBGenerateCommand.generateFTBChecks(removePermaHidden));
        } else {
            checks.put("FTBQuests", new HashMap<>());
        }

        checks.put("Advancements", generateAdvancementChecks());

        try {
            new File("output").mkdir();
            new File("output/archipelago_data.json").createNewFile();

            Writer writer = new FileWriter("output/archipelago_data.json");
            GsonBuilder builder = new GsonBuilder()
                    .disableHtmlEscaping()
                    .serializeNulls();
            if (!singleLine) {
                builder.setPrettyPrinting();
            }
            Gson gson = builder.create();
            gson.toJson(checks, writer);
            writer.close();
        } catch (IOException e) {
            context.getSource().sendSuccess(Component.literal(e.getMessage()), false);
            return 1;
        }
        context.getSource().sendSuccess(Component.literal("Finished writing to file."), false);
        return 0;
    }

    public static Map<String, AdvancementsCheck> generateAdvancementChecks() {
        Map<String, AdvancementsCheck> advancementsChecks = new HashMap<>();

        for (Advancement advancement : Archipelago.server.getAdvancements().getAllAdvancements()) {

            DisplayInfo display = advancement.getDisplay();
            if (display != null) {
                Advancement parent = advancement.getParent();
                String parent_id = null;
                if (parent != null) {
                    parent_id = parent.getId().toString();
                }
                if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                    advancementsChecks.put(advancement.getId().toString(), new AdvancementsCheck(
                            display.getFrame().getName(),
                            parent_id
                    ));
                }
            }
        }
        return advancementsChecks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // use first instance when dealing with conflicts
                        LinkedHashMap::new
                )
        );
    }
}
