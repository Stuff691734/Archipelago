package net.stuff691734.archipelago.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
    public static void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean singleLine = true;
        if (args.length >= 2) {
            singleLine = !CommandBase.parseBoolean(args[1]);
        }

        boolean removePermaHidden = true;
        if (args.length >= 3) {
            removePermaHidden = !CommandBase.parseBoolean(args[2]);
        }

        sender.sendMessage(new TextComponentString("Started writing to file."));

        Map<String, Check> checks = new LinkedHashMap<>(generateAdvancementChecks(server));


        try {
            new File("output").mkdir();
            new File("output/archipelago_data.json").createNewFile();

            Writer writer = new FileWriter("output/archipelago_data.json");
            GsonBuilder builder = new GsonBuilder()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(DependencyNotation.class, new DependencyNotationSerializer())
                    .serializeNulls();
            if (singleLine) {
                builder.setPrettyPrinting();
            }
            Gson gson = builder.create();
            gson.toJson(checks, writer);
            writer.close();
        } catch (IOException e) {
            sender.sendMessage(new TextComponentString(e.getMessage()));
        }
        sender.sendMessage(new TextComponentString("Finished writing to file."));
    }

    public static Map<String, AdvancementsCheck> generateAdvancementChecks(MinecraftServer server) {
        Map<String, AdvancementsCheck> advancementsChecks = new HashMap<>();

        for (Advancement advancement : server.getAdvancementManager().getAdvancements()) {

            DisplayInfo display = advancement.getDisplay();
            if (display != null) {
                Advancement parent = advancement.getParent();
                String parent_id = null;
                if (parent != null && parent.getDisplay() != null) {
                    parent_id = String.format("adv %s (%s)", parent.getId(), parent.getDisplay().getTitle().getUnformattedText());
                }
                Advancement root = Utils.getRoot(advancement);
                String tab;
                if (root.getDisplay() != null) {
                    tab = String.format("adv %s (%s)", root.getId(), root.getDisplay().getTitle().getUnformattedText());
                } else {
                    tab = String.format("adv %s (%s)", root.getId(), root.getId());
                }

                if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                    advancementsChecks.put(String.format("adv %s (%s)", advancement.getId(), display.getTitle().getUnformattedText()), new AdvancementsCheck(
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
