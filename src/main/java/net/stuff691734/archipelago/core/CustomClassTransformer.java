package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class CustomClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("net.minecraft.advancements.PlayerAdvancements")) {
            // not the class we are looking for, no changes.
            return basicClass;
        }
        return transformClass(basicClass);
    }

    public byte[] transformClass(byte[] basicClass) {
        // setup
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        // find method
        for (MethodNode method : classNode.methods) {
            // grantCriterion method
            if (method.name.equals("func_192750_a")) {
                // find last return
                // forge has an early return for fake players
                AbstractInsnNode currentNode = null;
                AbstractInsnNode sendArchipelagoAdvancementTarget = null;

                // find all isDone calls
                ArrayList<AbstractInsnNode> preventAdvancementTargets = new ArrayList<>();

                int index = -1;

                Iterator<AbstractInsnNode> iter = method.instructions.iterator();
                while (iter.hasNext()) {
                    index++;
                    currentNode = iter.next();

                    // found return
                    if (currentNode.getOpcode() == IRETURN) {
                        sendArchipelagoAdvancementTarget = currentNode;
                    }

                    // found isDone call
                    if (currentNode.getOpcode() == INVOKEVIRTUAL
                            && ((MethodInsnNode) currentNode).name.equals("func_192105_a")) {
                        preventAdvancementTargets.add(currentNode);
                    }
                }

                if (sendArchipelagoAdvancementTarget == null || index == -1) {
                    // return not found or loop not ran
                    return basicClass;
                }

                method.instructions.insertBefore(sendArchipelagoAdvancementTarget, sendArchipelagoAdvancement());

                preventAdvancementTargets.forEach((node) -> method.instructions.insert(node, preventAdvancement()));
            }
        }
        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList sendArchipelagoAdvancement() {
        InsnList newInstructionList = new InsnList();
        LabelNode L1 = new LabelNode();
        // L0
        LabelNode L0 = new LabelNode();
        newInstructionList.add(L0);
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192068_c", "()Lnet/minecraft/advancements/DisplayInfo;", false));
        newInstructionList.add(new JumpInsnNode(IFNULL, L1));

        // L2
        LabelNode L2 = new LabelNode();
        newInstructionList.add(L2);
        newInstructionList.add(new VarInsnNode(ALOAD, 4));
        newInstructionList.add(preventAdvancement());
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementProgress", "func_192105_a", "()Z", false));
        newInstructionList.add(new JumpInsnNode(IFEQ, L1));

        // L3
        LabelNode L3 = new LabelNode();
        newInstructionList.add(L3);
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "isConnected", "()Z", false));
        newInstructionList.add(new JumpInsnNode(IFEQ, L1));

        // L4
        LabelNode L4 = new LabelNode();
        newInstructionList.add(L4);
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "getDataPackage", "()Lshadow/archipelago/io/github/archipelagomw/parts/DataPackage;", false));
        newInstructionList.add(new LdcInsnNode("Modded Minecraft"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "shadow/archipelago/io/github/archipelagomw/parts/DataPackage", "getGame", "(Ljava/lang/String;)Lshadow/archipelago/io/github/archipelagomw/parts/Game;", false));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "shadow/archipelago/io/github/archipelagomw/parts/Game", "locationNameToId", "Ljava/util/Map;"));
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Long"));
        newInstructionList.add(new VarInsnNode(ASTORE, 6));

        // L5
        LabelNode L5 = new LabelNode();
        newInstructionList.add(L5);
        newInstructionList.add(new VarInsnNode(ALOAD, 6));
        newInstructionList.add(new JumpInsnNode(IFNULL, L1));

        // L6
        LabelNode L6 = new LabelNode();
        newInstructionList.add(L6);
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "getLocationManager", "()Lshadow/archipelago/io/github/archipelagomw/LocationManager;", false));
        newInstructionList.add(new VarInsnNode(ALOAD, 6));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "shadow/archipelago/io/github/archipelagomw/LocationManager", "checkLocation", "(J)Lshadow/archipelago/io/github/archipelagomw/APResult;", false));
        newInstructionList.add(new InsnNode(POP));

        // L7
        LabelNode L7 = new LabelNode();
        newInstructionList.add(L7);
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/ChecksState", "getServerState", "(Lnet/minecraft/server/MinecraftServer;)Lnet/stuff691734/archipelago/ChecksState;", false));
        newInstructionList.add(new VarInsnNode(ASTORE, 7));

        // L8
        LabelNode L8 = new LabelNode();
        newInstructionList.add(L8);
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
        newInstructionList.add(new LdcInsnNode("final_goal"));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        newInstructionList.add(new JumpInsnNode(IFEQ, L1));

        // L9
        LabelNode L9 = new LabelNode();
        newInstructionList.add(L9);
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
        newInstructionList.add(new FieldInsnNode(GETSTATIC, "shadow/archipelago/io/github/archipelagomw/ClientStatus", "CLIENT_GOAL", "Lshadow/archipelago/io/github/archipelagomw/ClientStatus;"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "setGameState", "(Lshadow/archipelago/io/github/archipelagomw/ClientStatus;)Lshadow/archipelago/io/github/archipelagomw/APResult;", false));
        newInstructionList.add(new InsnNode(POP));

        // L1
        newInstructionList.add(L1);

        return newInstructionList;
    }

    public InsnList preventAdvancement() {
        InsnList newInstructionList = new InsnList();
        LabelNode L1 = new LabelNode();

        // L0
        LabelNode L0 = new LabelNode();
        newInstructionList.add(L0);
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192068_c", "()Lnet/minecraft/advancements/DisplayInfo;", false));
        newInstructionList.add(new JumpInsnNode(IFNULL, L1));

        // L2
        LabelNode L2 = new LabelNode();
        newInstructionList.add(L2);

        newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/ChecksState", "getServerState", "(Lnet/minecraft/server/MinecraftServer;)Lnet/stuff691734/archipelago/ChecksState;", false));
        // gonna be consistent with where I store variables
        newInstructionList.add(new VarInsnNode(ASTORE, 7));

        LabelNode L4 = new LabelNode();

        // L3
        LabelNode L3 = new LabelNode();
        newInstructionList.add(L3);
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
        newInstructionList.add(new LdcInsnNode("unlock_type"));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new LdcInsnNode("tab"));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
        newInstructionList.add(new JumpInsnNode(IFEQ, L4));

        // L5
        LabelNode L5 = new LabelNode();
        newInstructionList.add(L5);
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "getRoot", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/Advancement;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
        newInstructionList.add(new JumpInsnNode(IFNE, L1));

        // L6
        LabelNode L6 = new LabelNode();
        newInstructionList.add(L6);
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new InsnNode(IRETURN));

        LabelNode L7 = new LabelNode();

        // L4
        newInstructionList.add(L4);
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
        newInstructionList.add(new LdcInsnNode("unlock_type"));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new LdcInsnNode("tree"));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
        newInstructionList.add(new JumpInsnNode(IFEQ, L7));

        LabelNode L9 = new LabelNode();

        // L8
        LabelNode L8 = new LabelNode();
        newInstructionList.add(L8);
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "getRoot", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/Advancement;", false));
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new JumpInsnNode(IF_ACMPNE, L9));

        // L10
        LabelNode L10 = new LabelNode();
        newInstructionList.add(L10);
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
        newInstructionList.add(new JumpInsnNode(IFNE, L1));

        // L11
        LabelNode L11 = new LabelNode();
        newInstructionList.add(L11);
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new InsnNode(IRETURN));

        // L9
        newInstructionList.add(L9);
        newInstructionList.add(new VarInsnNode(ALOAD, 1));
        newInstructionList.add(new VarInsnNode(ASTORE, 8));

        LabelNode L13 = new LabelNode();

        // L12
        LabelNode L12 = new LabelNode();
        newInstructionList.add(L12);
        newInstructionList.add(new VarInsnNode(ALOAD, 8));
        newInstructionList.add(new JumpInsnNode(IFNULL, L13));

        // L14
        LabelNode L14 = new LabelNode();
        newInstructionList.add(L14);
        newInstructionList.add(new VarInsnNode(ALOAD, 8));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192070_b", "()Lnet/minecraft/advancements/Advancement;", false));
        newInstructionList.add(new VarInsnNode(ASTORE, 8));

        // L15
        LabelNode L15 = new LabelNode();
        newInstructionList.add(L15);
        newInstructionList.add(new VarInsnNode(ALOAD, 8));
        newInstructionList.add(new JumpInsnNode(IFNULL, L12));

        // L16
        LabelNode L16 = new LabelNode();
        newInstructionList.add(L16);
        newInstructionList.add(new VarInsnNode(ALOAD, 7));
        newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
        newInstructionList.add(new VarInsnNode(ALOAD, 8));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
        newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
        newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
        newInstructionList.add(new JumpInsnNode(IFNE, L12));

        // L17
        LabelNode L17 = new LabelNode();
        newInstructionList.add(L17);
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new InsnNode(IRETURN));

        // L13
        newInstructionList.add(L13);
        newInstructionList.add(new JumpInsnNode(GOTO, L1));

        // L7
        newInstructionList.add(L7);

        // L18
        LabelNode L18 = new LabelNode();
        newInstructionList.add(L18);
        newInstructionList.add(new InsnNode(ICONST_0));
        newInstructionList.add(new InsnNode(IRETURN));

        // L1
        newInstructionList.add(L1);

        return newInstructionList;
    }
}
