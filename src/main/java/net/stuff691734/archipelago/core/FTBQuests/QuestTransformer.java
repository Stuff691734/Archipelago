package net.stuff691734.archipelago.core.FTBQuests;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class QuestTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("com.feed_the_beast.ftbquests.quest.Quest")) {
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
            if (method.name.equals("onCompleted")) {
                AbstractInsnNode sendArchipelagoQuestTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == RETURN) {
                        sendArchipelagoQuestTarget = node;
                    }
                }
                if (sendArchipelagoQuestTarget != null) {
                    method.instructions.insertBefore(sendArchipelagoQuestTarget, sendArchipelagoQuest());
                }
            }
            if (method.name.equals("getUnclaimedRewards")) {
                AbstractInsnNode isCompleteTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("isComplete")) {
                        isCompleteTarget = node;
                    }
                }
                if (isCompleteTarget != null) {
                    method.instructions.insertBefore(isCompleteTarget, isQuestComplete());
                    method.instructions.remove(isCompleteTarget);
                }
            }

            if (method.name.equals("canStartTasks")) {
                AbstractInsnNode canStartTasksTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("areDependenciesComplete")) {
                        canStartTasksTarget = node;
                    }
                }
                if (canStartTasksTarget != null) {
                    method.instructions.insert(canStartTasksTarget, canStartTasks());
                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList sendArchipelagoQuest() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "sendArchipelagoQuest", "(Lcom/feed_the_beast/ftbquests/quest/Quest;)V", false));

        return instructions;
    }

    public InsnList isQuestComplete() {
        InsnList instructions = new InsnList();

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "isQuestRewardAvailable", "(Lcom/feed_the_beast/ftbquests/quest/Quest;Lcom/feed_the_beast/ftbquests/quest/QuestData;)Z", false));

        return instructions;
    }

    public InsnList canStartTasks() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "isQuestStartable", "(ZLcom/feed_the_beast/ftbquests/quest/Quest;)Z", false));

        return instructions;
    }
}
