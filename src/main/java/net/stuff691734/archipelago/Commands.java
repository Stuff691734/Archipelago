package net.stuff691734.archipelago;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.text.LiteralText;
import net.stuff691734.archipelago.archipelagoData.Check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("archipelago")
                .then(literal("connect")
                    .then(argument("Name", StringArgumentType.word())
                        .then(argument("WSAddress", StringArgumentType.greedyString())
                            .executes(context -> {
                                final String Name = StringArgumentType.getString(context, "Name");
                                final String WebSocketAddress = StringArgumentType.getString(context, "WSAddress");
                                Archipelago.client.setName(Name);
                                try {
                                    Archipelago.client.connect(WebSocketAddress);
                                } catch (URISyntaxException e) {
                                    context.getSource().sendFeedback(new LiteralText("Invalid server address"), false);
                                    return 1;
                                }
                                context.getSource().sendFeedback(new LiteralText("Connected"), false);
                                return 0;
                            })
                        )
                    )
                )
                .then(literal("disconnect")
                    .executes(context -> {
                        Archipelago.client.disconnect();
                        return 0;
                    })
                )
                .then(literal("generate")
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Started writing to file."), false);

                        Map<String, Check> checks = new HashMap<>();

                        for (Advancement advancement : Archipelago.server.getAdvancementManager().getAdvancements()) {

                            AdvancementDisplay display = advancement.getDisplay();
                            if (display != null) {
                                Advancement parent = advancement.getParent();
                                String parent_id = null;
                                if (parent != null) {
                                    parent_id = parent.getId().toString();
                                }
                                if (parent_id != null && parent_id.equals("minecraft:recipes/root")) {
                                    continue;
                                }

                                checks.put(advancement.getId().toString(),new Check(
                                        display.getFrame().getId(),
                                        parent_id
                                ));
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
                            context.getSource().sendFeedback(new LiteralText(e.getMessage()), false);
                            return 1;
                        }
                        context.getSource().sendFeedback(new LiteralText("Finished writing to file."), false);
                        return 0;
                    })
                )
                .then(literal("get")
                    .executes(context -> {
                        ChecksState checkState = ChecksState.getServerState(Archipelago.server);
                        context.getSource().sendFeedback(new LiteralText(checkState.checks.toString()), false);
                        context.getSource().sendFeedback(new LiteralText(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), false);
                        return 0;
                    })
                    .then(argument("check", StringArgumentType.greedyString())
                        .executes(context -> {
                            final String checkName = StringArgumentType.getString(context, "check");
                            ChecksState checkState = ChecksState.getServerState(Archipelago.server);
                            context.getSource().sendFeedback(new LiteralText(checkState.checks.getOrDefault(checkName, false).toString()), false);
                            return 0;
                        })
                    )
                )
                // Dev Feature: Not needed in final update/should read from file
                .then(literal("add")
                    .then(argument("check", StringArgumentType.greedyString())
                        .executes(context -> {
                            final String checkName = StringArgumentType.getString(context, "check");
                            ChecksState checkState = ChecksState.getServerState(Archipelago.server);
                            checkState.checks.put(checkName, true);
                            return 0;
                        })
                    )
                )
            );
        });
    }

}