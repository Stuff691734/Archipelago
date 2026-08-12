package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class PlayerAdvancementsTransformer implements IClassTransformer {
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
        classNode.methods.forEach((method) -> {
            // PlayerAdvancements.load
            if (method.name.equals("func_192740_f")) {
                AbstractInsnNode showAllAdvancementsTarget = null;
                AbstractInsnNode loadAllAdvancementsTarget = null;

                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // PlayerAdvancement.startProgress
                    if (node.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)node).name.equals("func_192743_a")) {
                        showAllAdvancementsTarget = node;
                    }

                    // Map.entrySet
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode)node).name.equals("entrySet")) {
                        loadAllAdvancementsTarget = node;
                    }
                }

                if (showAllAdvancementsTarget != null) {
                    method.instructions.insert(showAllAdvancementsTarget, showAllAdvancements());
                }
                if (loadAllAdvancementsTarget != null) {
                    method.instructions.insertBefore(loadAllAdvancementsTarget, loadAllAdvancements());
                }

                method.instructions.insert(loadAdvancementsOnFirstJoin());
            }
            // PlayerAdvancements.shouldBeVisible
            if (method.name.equals("func_192738_c")) {
                // inserts at start of instructions
                method.instructions.insert(shouldBeVisible());
            }
            // PlayerAdvancements.grantCriterion
            if (method.name.equals("func_192750_a")) {
                AbstractInsnNode sendArchipelagoAdvancementTarget = null;

                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (node.getOpcode() == IRETURN) {
                        sendArchipelagoAdvancementTarget = node;
                    }

                    // AdvancementProgress.isDone
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("func_192105_a")) {
                        method.instructions.insert(node, preventAdvancement());
                    }
                }
                if (sendArchipelagoAdvancementTarget != null) {
                    method.instructions.insertBefore(sendArchipelagoAdvancementTarget, sendArchipelagoAdvancement());
                }
            }
        });

        classNode.methods.add(archipelago$lambda$loadAllAdvancements$0());

        classNode.methods.add(archipelago$ensureVisibility());
        classNode.interfaces.add("net/stuff691734/archipelago/mixin/PlayerAdvancementAccessor");

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public MethodNode archipelago$lambda$loadAllAdvancements$0() {
        MethodNode method = new MethodNode(ACC_PRIVATE | ACC_SYNTHETIC, "archipelago$lambda$loadAllAdvancements$0", "(Ljava/util/Map;Lnet/minecraft/advancements/Advancement;)V", null, null);

        InsnList instructions = new InsnList();
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new VarInsnNode(ALOAD, 2));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 2));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", "func_192747_a", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/AdvancementProgress;", false));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "putIfAbsent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        instructions.add(new InsnNode(POP));

        instructions.add(new InsnNode(RETURN));

        method.instructions.add(instructions);

        return method;
    }

    public InsnList showAllAdvancements() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/advancements/PlayerAdvancements", "field_192761_i", "Ljava/util/Set;"));
        instructions.add(new VarInsnNode(ALOAD, 6));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true));
        instructions.add(new InsnNode(POP));
//
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 6));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "func_192742_b", "(Lnet/minecraft/advancements/Advancement;)V", false));

        return instructions;
    }

    public InsnList loadAllAdvancements() {
        InsnList instructions = new InsnList();

        LabelNode L1 = new LabelNode();
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Archipelago", "getServer", "()Lnet/minecraft/server/MinecraftServer;", false));
        instructions.add(new JumpInsnNode(IFNULL, L1));

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Archipelago", "getServer", "()Lnet/minecraft/server/MinecraftServer;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", "func_191949_aK", "()Lnet/minecraft/advancements/AdvancementManager;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementManager", "func_192780_b", "()Ljava/lang/Iterable;", false));
        instructions.add(new InsnNode(SWAP));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new InsnNode(SWAP));
        // // instructions.add(new VarInsnNode(ALOAD, 1));

        instructions.add(new InvokeDynamicInsnNode(
                "accept",
                "(Lnet/minecraft/advancements/PlayerAdvancements;Ljava/util/Map;)Ljava/util/function/Consumer;",
                new Handle(H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                Type.getType("(Ljava/lang/Object;)V"),
                new Handle(H_INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "archipelago$lambda$loadAllAdvancements$0", "(Ljava/util/Map;Lnet/minecraft/advancements/Advancement;)V", false),
                Type.getType("(Lnet/minecraft/advancements/Advancement;)V")
        ));

        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "forEach", "(Ljava/util/function/Consumer;)V", true));

        instructions.add(new VarInsnNode(ALOAD, 2));
        instructions.add(L1);
        return instructions;
    }

    public InsnList shouldBeVisible() {
        InsnList instructions = new InsnList();

        LabelNode L1 = new LabelNode();

        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));
        instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "shouldShowAdvancement", "(Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Z", false));
        instructions.add(new JumpInsnNode(IFEQ, L1));

        instructions.add(new InsnNode(ICONST_1));
        instructions.add(new InsnNode(IRETURN));

        instructions.add(L1);

        return instructions;
    }

    public InsnList sendArchipelagoAdvancement() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", "func_192747_a", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/AdvancementProgress;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementProgress", "func_192105_a", "()Z", false));
        instructions.add(preventAdvancement());

        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelagoLib/archipelagoClient/ArchipelagoClient;"));
        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelagoLib/CheckType", "ADVANCEMENT", "Lnet/stuff691734/archipelagoLib/CheckType;"));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/CheckType", "addPrefix", "(Ljava/lang/String;)Ljava/lang/String;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/archipelagoClient/ArchipelagoClient", "sendCheck", "(Ljava/lang/String;)V", false));

        return instructions;
    }

    public InsnList preventAdvancement() {
        InsnList instructions = new InsnList();

        LabelNode L1 = new LabelNode();

        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));
        instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "isAdvancementCompletable", "(Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Z", false));
        instructions.add(new JumpInsnNode(IFNE, L1));

        instructions.add(new InsnNode(ICONST_0));
        instructions.add(new InsnNode(IRETURN));

        instructions.add(L1);

        return instructions;
    }

    public MethodNode archipelago$ensureVisibility() {
        MethodNode method = new MethodNode(ACC_PUBLIC, "archipelago$ensureVisibility", "(Lnet/minecraft/advancements/Advancement;)V", null, null);

        InsnList instructions = new InsnList();
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "func_192742_b", "(Lnet/minecraft/advancements/Advancement;)V", false));
        instructions.add(new InsnNode(RETURN));

        method.instructions.add(instructions);

        return method;
    }

    public InsnList loadAdvancementsOnFirstJoin() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/advancements/PlayerAdvancements", "field_192757_e", "Ljava/io/File;"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/File", "isFile", "()Z", false));
        LabelNode L1 = new LabelNode();
        instructions.add(new JumpInsnNode(IFNE, L1));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", "func_192749_b", "()V", false));
        instructions.add(L1);

        return instructions;
    }
}