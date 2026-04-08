package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import net.stuff691734.archipelago.mixin.PlayerAdvancementAccessor;
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

                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode)node).name.equals("collect")) {
                        loadAllAdvancementsTarget = node;
                    }
                }

                if (showAllAdvancementsTarget != null) {
                    method.instructions.insert(showAllAdvancementsTarget, showAllAdvancements());
                }
                if (loadAllAdvancementsTarget != null) {
                    method.instructions.remove(loadAllAdvancementsTarget.getPrevious().getPrevious());
                    method.instructions.remove(loadAllAdvancementsTarget.getPrevious());
                    method.instructions.insert(loadAllAdvancementsTarget, loadAllAdvancements());
                    method.instructions.remove(loadAllAdvancementsTarget);
                }
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

        classNode.methods.add(lambda$forEach());

        classNode.methods.add(archipelago$ensureVisibility());
        classNode.interfaces.add(PlayerAdvancementAccessor.class.getName().replace(".", "/"));

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public MethodNode lambda$forEach() {
        MethodNode method = new MethodNode(ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC, "lambda$forEach", "(Ljava/util/Map;Ljava/util/Map$Entry;)V", null, null);

        InsnList instructions = new InsnList();
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/advancements/AdvancementProgress"));
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

        instructions.add(new TypeInsnNode(NEW, "java/util/HashMap"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false));
        instructions.add(new VarInsnNode(ASTORE, 5));

        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", "func_191949_aK", "()Lnet/minecraft/advancements/AdvancementManager;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementManager", "func_192780_b", "()Ljava/lang/Iterable;", false));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true));
        instructions.add(new VarInsnNode(ASTORE, 4));

        LabelNode afterLoop = new LabelNode();

        LabelNode startLoop = new LabelNode();
        instructions.add(startLoop);
        instructions.add(new VarInsnNode(ALOAD, 4));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true));
        instructions.add(new JumpInsnNode(IFEQ, afterLoop));
        instructions.add(new VarInsnNode(ALOAD, 4));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true));
        instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/advancements/Advancement"));
        instructions.add(new VarInsnNode(ASTORE, 6));

        instructions.add(new VarInsnNode(ALOAD, 5));
        instructions.add(new VarInsnNode(ALOAD, 6));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 6));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "func_192747_a", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/AdvancementProgress;", false));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
        instructions.add(new InsnNode(POP));

        instructions.add(new JumpInsnNode(GOTO, startLoop));

        instructions.add(afterLoop);

        instructions.add(new VarInsnNode(ALOAD, 3));
        instructions.add(new VarInsnNode(ALOAD, 5));
        instructions.add(new InvokeDynamicInsnNode(
                "accept",
                "(Ljava/util/Map;)Ljava/util/function/Consumer;",
                new Handle(H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                Type.getType("(Ljava/lang/Object;)V"),
                new Handle(H_INVOKESTATIC, "net/minecraft/advancements/PlayerAdvancements", "lambda$forEach", "(Ljava/util/Map;Ljava/util/Map$Entry;)V", false),
                Type.getType("(Ljava/util/Map$Entry;)V")
        ));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/stream/Stream", "forEach", "(Ljava/util/function/Consumer;)V", true));

        instructions.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new VarInsnNode(ALOAD, 5));
        instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;", true));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V", false));

        return instructions;
    }

    public InsnList shouldBeVisible() {
        InsnList instructions = new InsnList();

        LabelNode continueExecution = new LabelNode();

        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "shouldBeVisible", "(Lnet/minecraft/advancements/Advancement;)Z", false));
        instructions.add(new JumpInsnNode(IFEQ, continueExecution));

        instructions.add(new InsnNode(ICONST_1));
        instructions.add(new InsnNode(IRETURN));

        instructions.add(continueExecution);

        return instructions;
    }

    public InsnList sendArchipelagoAdvancement() {
        InsnList instructions = new InsnList();

        LabelNode continueExecution = new LabelNode();

        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "preventAdvancement", "(Lnet/minecraft/advancements/Advancement;)Z", false));
        instructions.add(new JumpInsnNode(IFEQ, continueExecution));

        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "sendArchipelagoAdvancement", "(Lnet/minecraft/advancements/Advancement;)V", false));

        instructions.add(continueExecution);

        return instructions;
    }

    public InsnList preventAdvancement() {
        InsnList instructions = new InsnList();

        LabelNode continueExecution = new LabelNode();

        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "preventAdvancement", "(Lnet/minecraft/advancements/Advancement;)Z", false));
        instructions.add(new JumpInsnNode(IFNE, continueExecution));

        instructions.add(new InsnNode(ICONST_0));
        instructions.add(new InsnNode(IRETURN));

        instructions.add(continueExecution);

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
}