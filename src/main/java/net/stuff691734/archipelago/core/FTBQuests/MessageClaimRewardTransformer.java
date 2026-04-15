package net.stuff691734.archipelago.core.FTBQuests;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class MessageClaimRewardTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("com.feed_the_beast.ftbquests.net.MessageClaimReward")) {
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
            if (method.name.equals("onMessage")) {
                AbstractInsnNode claimRewardsTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("isComplete")) {
                        claimRewardsTarget = node;
                    }
                }
                if (claimRewardsTarget != null) {
                    method.instructions.insert(claimRewardsTarget, claimRewards());
                    method.instructions.remove(claimRewardsTarget);
                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList claimRewards() {
        InsnList instructions = new InsnList();

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "isQuestRewardAvailable", "(Lcom/feed_the_beast/ftbquests/quest/Quest;Lcom/feed_the_beast/ftbquests/quest/QuestData;)Z", false));

        return instructions;
    }
}