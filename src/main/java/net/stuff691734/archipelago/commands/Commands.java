//package net.stuff691734.archipelago.commands;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.minecraft.advancements.Advancement;
//import net.minecraft.advancements.DisplayInfo;
//import net.minecraft.util.text.TextComponentString;
//import net.stuff691734.archipelago.Archipelago;
//import net.stuff691734.archipelago.ChecksState;
//import net.stuff691734.archipelago.archipelagoData.Check;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class Commands {
//    public static void register(CommandDispatcher<CommandSource> dispatcher) {
//        dispatcher.register(literal("archipelago")
//                .then(literal("connect")
//                        .then(argument("Name", StringArgumentType.word())
//                                .then(argument("WSAddress", StringArgumentType.greedyString())
//                                        .executes(context -> {
//                                            final String Name = StringArgumentType.getString(context, "Name");
//                                            final String WebSocketAddress = StringArgumentType.getString(context, "WSAddress");
//                                            Archipelago.client.setName(Name);
//                                            try {
//                                                Archipelago.client.connect(WebSocketAddress);
//                                            } catch (URISyntaxException e) {
//
//                                                context.getSource().sendFeedback(new TextComponentString("Invalid server address"), false);
//                                                return 1;
//                                            }
//                                            context.getSource().sendFeedback(new TextComponentString("Connected"), false);
//                                            return 0;
//                                        })
//                                )
//                        )
//                )
//                .then(literal("disconnect")
//                        .executes(context -> {
//                            Archipelago.client.disconnect();
//                            return 0;
//                        })
//                )
//                .then(literal("generate")
//                        .executes(context -> {
//                            context.getSource().sendFeedback(new TextComponentString("Started writing to file."), false);
//
//                            Map<String, Check> checks = new HashMap<>();
//
//                            for (Advancement advancement : Archipelago.server.getAdvancementManager().getAllAdvancements()) {
//
//                                DisplayInfo display = advancement.getDisplay();
//                                if (display != null) {
//                                    Advancement parent = advancement.getParent();
//                                    String parent_id = null;
//                                    if (parent != null) {
//                                        parent_id = parent.getId().toString();
//                                    }
//                                    if (parent_id == null || !parent_id.equals("minecraft:recipes/root")) {
//                                        checks.put(advancement.getId().toString(), new Check(
//                                                display.getFrame().getName(),
//                                                parent_id
//                                        ));
//                                    }
//                                }
//                            }
//                            try {
//                                new File("output").mkdir();
//                                new File("output/archipelago_data.json").createNewFile();
//                                Writer writer = new FileWriter("output/archipelago_data.json");
//                                Gson gson = new GsonBuilder()
//                                        .setPrettyPrinting()
//                                        .disableHtmlEscaping()
//                                        .serializeNulls()
//                                        .create();
//                                gson.toJson(checks, writer);
//                                writer.close();
//
//                            } catch (IOException e) {
//                                context.getSource().sendFeedback(new TextComponentString(e.getMessage()), false);
//                                return 1;
//                            }
//                            context.getSource().sendFeedback(new TextComponentString("Finished writing to file."), false);
//                            return 0;
//                        })
//                )
//                .then(literal("get")
//                        .executes(context -> {
//                            ChecksState checkState = ChecksState.getServerState(Archipelago.server);
//                            context.getSource().sendFeedback(new TextComponentString(checkState.checks.toString()), false);
//                            context.getSource().sendFeedback(new TextComponentString(Archipelago.client.getItemManager().getReceivedItemIDs().toString()), false);
//                            return 0;
//                        })
//                        .then(argument("check", StringArgumentType.greedyString())
//                                .executes(context -> {
//                                    final String checkName = StringArgumentType.getString(context, "check");
//                                    ChecksState checkState = ChecksState.getServerState(Archipelago.server);
//                                    context.getSource().sendFeedback(new TextComponentString(checkState.checks.getOrDefault(checkName, false).toString()), false);
//                                    return 0;
//                                })
//                        )
//                )
//                // Dev Feature: Not needed in final update/should read from file
//                .then(literal("add")
//                        .then(argument("check", StringArgumentType.greedyString())
//                                .executes(context -> {
//                                    final String checkName = StringArgumentType.getString(context, "check");
//                                    ChecksState checkState = ChecksState.getServerState(Archipelago.server);
//                                    checkState.checks.put(checkName, true);
//                                    return 0;
//                                })
//                        )
//                )
//        );
//    }
//}