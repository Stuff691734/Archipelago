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

        classNode.methods.forEach((method) -> {
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

        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList isUnlocked() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new InsnNode(SWAP));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "isUnlocked", "(Lbetterquesting/questing/QuestInstance;Z)Z", false));

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
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "sendCheck", "(Lbetterquesting/questing/QuestInstance;)V", false));

        return instructions;
    }
}
