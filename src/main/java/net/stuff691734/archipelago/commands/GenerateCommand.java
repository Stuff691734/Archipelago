package net.stuff691734.archipelago.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.stuff691734.archipelago.archipelagoData.Check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class GenerateCommand {
    public static void execute(MinecraftServer server, ICommandSender sender) {
        sender.sendMessage(new TextComponentString("Started writing to file."));

        Map<String, Check> checks = new HashMap<>();

        for (Advancement advancement : server.getAdvancementManager().getAdvancements()) {

            DisplayInfo display = advancement.getDisplay();
            if (display != null) {
                Advancement parent = advancement.getParent();
                String parent_id = null;
                if (parent != null) {
                    parent_id = parent.getId().toString();
                }
                if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
                    checks.put(advancement.getId().toString(), new Check(
                            display.getFrame().getName(),
                            parent_id
                    ));
                }
            }
        }
        try {
            new File("output").mkdir();
            new File("output/archipelago_data.json").createNewFile();
            Writer writer = new FileWriter("output/archipelago_data.json");
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();
            gson.toJson(checks, writer);
            writer.close();

        } catch (IOException e) {
            sender.sendMessage(new TextComponentString(e.getMessage()));
            return;
        }
        sender.sendMessage(new TextComponentString("Finished writing to file."));
    }
}
