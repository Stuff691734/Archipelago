package net.stuff691734.archipelago.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.Utils;
import net.stuff691734.archipelago.archipelagoData.AdvancementsCheck;
import net.stuff691734.archipelago.archipelagoData.Check;
import net.stuff691734.archipelago.archipelagoData.DependencyNotation;
import net.stuff691734.archipelago.archipelagoData.DependencyNotationSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateCommand {
    public static int execute(CommandContext<CommandSource> context, boolean singleLine, boolean removePermaHidden) {
        context.getSource().sendFeedback(new TextComponentString("Started writing to file."), false);

        Map<String, Check> checks = new LinkedHashMap<>(generateAdvancementChecks(context.getSource().getServer()));


        try {
            new File("output").mkdir();
            new File("output/archipelago_data.json").createNewFile();

            Writer writer = new FileWriter("output/archipelago_data.json");
            GsonBuilder builder = new GsonBuilder()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(DependencyNotation.class, new DependencyNotationSerializer())
                    .serializeNulls();
            if (!singleLine) {
                builder.setPrettyPrinting();
            }
            Gson gson = builder.create();
            gson.toJson(checks, writer);
            writer.close();
        } catch (IOException e) {
            context.getSource().sendFeedback(new TextComponentString(e.getMessage()), false);
            return 1;
        }
        context.getSource().sendFeedback(new TextComponentString("Finished writing to file."), false);
        return 0;
    }

    public static Map<String, AdvancementsCheck> generateAdvancementChecks(MinecraftServer server) {
        Map<String, AdvancementsCheck> advancementsChecks = new HashMap<>();

        for (Advancement advancement : server.getAdvancementManager().getAllAdvancements()) {

            DisplayInfo display = advancement.getDisplay();
            if (display != null) {
                Advancement parent = advancement.getParent();
                String parent_id = null;
                if (parent != null && parent.getDisplay() != null) {
                    parent_id = String.format("adv %s (%s)", parent.getId(), parent.getDisplay().getTitle().getUnformattedComponentText());
                }
                Advancement root = Utils.getRoot(advancement);
                String tab;
                if (root.getDisplay() != null) {
                    tab = String.format("adv %s (%s)", root.getId(), root.getDisplay().getTitle().getUnformattedComponentText());
                } else {
                    tab = String.format("adv %s (%s)", root.getId(), root.getId());
                }

                if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                    advancementsChecks.put(String.format("adv %s (%s)", advancement.getId(), display.getTitle().getUnformattedComponentText()), new AdvancementsCheck(
                            display.getFrame().getName(),
                            parent_id,
                            tab
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
