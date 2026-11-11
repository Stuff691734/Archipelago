package net.stuff691734.archipelago.core.BetterQuesting;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class QuestInstanceTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("betterquesting.questing.QuestInstance")) {
            // not the class we are looking for, no changes.
            return basicClass;
        }
        return transformClass(basicClass);
    }

    public byte[] transformClass(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        classNode.fields.add(new FieldNode(ACC_PRIVATE | ACC_FINAL, "claimedUsers", "Ljava/util/ArrayList;", "Ljava/util/ArrayList<Lbetterquesting/misc/UserEntry;>;", null));

        classNode.methods.forEach((method) -> {
            if (method.name.equals("<init>")) {
                AbstractInsnNode returnNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // put it after the last PUTFIELD instruction
                    if (node.getOpcode() == PUTFIELD) {
                        returnNode = node;
                    }
                }
                if (returnNode != null) {
                    method.instructions.insert(returnNode, initClaimedUsers());
                }
            }

            if (method.name.equals("getStandardTooltip")) {
                AbstractInsnNode returnNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ARETURN) {
                        returnNode = node;
                    }
                }
                if (returnNode != null) {
                    method.instructions.insertBefore(returnNode, addToTooltip());
                }
            }

            if (method.name.equals("getState")) {
                method.instructions = getState();
            }

            if (method.name.equals("isUnlocked")) {
                ArrayList<AbstractInsnNode> targetNodes = new ArrayList<>();
                AbstractInsnNode getResultTargetNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == IRETURN) {
                        targetNodes.add(node);
                    }
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("getResult")) {
                        getResultTargetNode = node;
                    }
                }
                for (AbstractInsnNode node : targetNodes) {
                    method.instructions.insertBefore(node, isUnlocked());
                }
                if (getResultTargetNode != null) {
                    method.instructions.insert(getResultTargetNode, getResult());
                    method.instructions.remove(getResultTargetNode);
                }
            }

            if (method.name.equals("setComplete")) {
                method.instructions.insert(sendCheck());
            }

            if (
                method.name.equals("hasClaimed") ||
                method.name.equals("claimReward")
            ) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETFIELD && ((FieldInsnNode)node).name.equals("completeUsers")) {
                        ((FieldInsnNode)node).name = "claimedUsers";
                    }
                }
            }

            if (
                method.name.equals("hasClaimed") ||
                method.name.equals("canClaim") ||
                method.name.equals("claimReward")

            ) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)node).name.equals("GetUserEntry")) {
                        ((MethodInsnNode)node).name = "GetUserClaimedEntry";
                    }
                }
            }

            if (method.name.equals("canClaim")) {
                AbstractInsnNode canSubmitNode = null;
                ArrayList<AbstractInsnNode> returnNodes = new ArrayList<>();
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("canSubmit")) {
                        canSubmitNode = node;
                    }
                    if (node.getOpcode() == IRETURN) {
                        returnNodes.add(node);
                    }
                }
                for (AbstractInsnNode node : returnNodes) {
                    method.instructions.insertBefore(node, canClaim());
                }
                if (canSubmitNode != null) {
                    method.instructions.insert(canSubmitNode, replaceCanSubmitOutput());
                }
            }

            if (method.name.equals("canSubmit")) {
                ArrayList<AbstractInsnNode> returnNodes = new ArrayList<>();
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == IRETURN) {
                        returnNodes.add(node);
                    }
                }
                for (AbstractInsnNode node : returnNodes) {
                    method.instructions.insertBefore(node, canSubmit());
                }
            }

            if (method.name.equals("writeToJson_Progress")) {
                AbstractInsnNode returnNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("canSubmit")) {
                        returnNode = node;
                    }
                }
                if (returnNode != null) {
                    method.instructions.insert(returnNode, writeClaimedProgress());
                }
            }

            if (method.name.equals("readFromJson_Progress")) {
                // insert at start after try catch block?
                AbstractInsnNode startNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD) {
                        startNode = node;
                        break;
                    }
                }
                if (startNode != null) {
                    method.instructions.insert(startNode, readClaimedProgress());
                }
            }
        });

        classNode.methods.add(GetUserClaimedEntry());

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public MethodNode GetUserClaimedEntry() {
        MethodNode GetUserClaimedEntry = new MethodNode(
                ACC_PRIVATE,
                "GetUserClaimedEntry",
                "(Ljava/util/UUID;)Lbetterquesting/misc/UserEntry;",
                null,
                null
        );

        GetUserClaimedEntry.instructions.add(new VarInsnNode(ALOAD, 0));
        GetUserClaimedEntry.instructions.add(new FieldInsnNode(GETFIELD, "betterquesting/questing/QuestInstance", "claimedUsers", "Ljava/util/ArrayList;"));
        GetUserClaimedEntry.instructions.add(new VarInsnNode(ALOAD, 1));
        GetUserClaimedEntry.instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "GetUserClaimedEntry", "(Ljava/util/ArrayList;Ljava/util/UUID;)Lbetterquesting/misc/UserEntry;", false));
        GetUserClaimedEntry.instructions.add(new InsnNode(ARETURN));

        return GetUserClaimedEntry;
    }

    public InsnList initClaimedUsers() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
        instructions.add(new FieldInsnNode(PUTFIELD, "betterquesting/questing/QuestInstance", "claimedUsers", "Ljava/util/ArrayList;"));

        return instructions;
    }

    public InsnList addToTooltip() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 2)); // list
        instructions.add(new VarInsnNode(ALOAD, 0)); // this
        instructions.add(new VarInsnNode(ALOAD, 3)); // playerId
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "addToTooltip", "(Ljava/util/List;Lbetterquesting/api/questing/IQuest;Ljava/util/UUID;)V", false));

        return instructions;
    }

    public InsnList getState() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "getState", "(Lbetterquesting/api/questing/IQuest;Ljava/util/UUID;)Lbetterquesting/api/enums/EnumQuestState;", false));
        instructions.add(new InsnNode(ARETURN));

        return instructions;
    }

    public InsnList isUnlocked() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new InsnNode(SWAP));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "isUnlocked", "(Lbetterquesting/api/questing/IQuest;Z)Z", false));

        return instructions;
    }

    public InsnList getResult() {
        InsnList instructions = new InsnList();

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "getResult", "(Lbetterquesting/api/enums/EnumLogic;II)Z", false));

        return instructions;
    }

    public InsnList sendCheck() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "sendCheck", "(Lbetterquesting/api/questing/IQuest;)V", false));

        return instructions;
    }

    public InsnList replaceCanSubmitOutput() {
        InsnList instructions = new InsnList();

        instructions.add(new InsnNode(POP));
        instructions.add(new InsnNode(ICONST_0));

        return instructions;
    }

    public InsnList canClaim() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new InsnNode(SWAP));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "canClaim", "(Lbetterquesting/api/questing/IQuest;ZLnet/minecraft/entity/player/EntityPlayer;)Z", false));

        return instructions;
    }

    public InsnList canSubmit() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new InsnNode(SWAP));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "canSubmit", "(Lbetterquesting/api/questing/IQuest;ZLnet/minecraft/entity/player/EntityPlayer;)Z", false));

        return instructions;
    }

    public InsnList writeClaimedProgress() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "betterquesting/questing/QuestInstance", "claimedUsers", "Ljava/util/ArrayList;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "writeProgress", "(Lcom/google/gson/JsonObject;Ljava/util/ArrayList;)Lcom/google/gson/JsonObject;", false));

        return instructions;
    }

    public InsnList readClaimedProgress() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "betterquesting/questing/QuestInstance", "claimedUsers", "Ljava/util/ArrayList;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "readProgress", "(Lcom/google/gson/JsonObject;Ljava/util/ArrayList;)V", false));

        return instructions;
    }
}
