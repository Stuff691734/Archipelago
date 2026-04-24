package net.stuff691734.archipelago.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
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
        context.getSource().sendSuccess(() -> Component.literal("Started writing to file."), false);

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
            context.getSource().sendSuccess(() -> Component.literal(e.getMessage()), false);
            return 1;
        }
        context.getSource().sendSuccess(() -> Component.literal("Finished writing to file."), false);
        return 0;
    }

    public static Map<String, AdvancementsCheck> generateAdvancementChecks() {
        Map<String, AdvancementsCheck> advancementsChecks = new HashMap<>();

        for (AdvancementHolder advancement : Archipelago.server.getAdvancements().getAllAdvancements()) {

            advancement.value().display().ifPresent(display -> {
                AdvancementNode placedAdvancement = Archipelago.server.getAdvancements().tree().get(advancement);
                if (placedAdvancement != null) {
                    AdvancementNode parent = placedAdvancement.parent();
                    String parent_id = null;
                    if (parent != null) {
                        parent_id = parent.holder().id().toString();
                    }
                    if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                        advancementsChecks.put(advancement.id().toString(), new AdvancementsCheck(
                                display.getType().getSerializedName(),
                                parent_id
                        ));
                    }
                }
            });
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
