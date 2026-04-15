package net.stuff691734.archipelago.core.FTBQuests;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ButtonRewardTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("com.feed_the_beast.ftbquests.gui.tree.ButtonReward")) {
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
            if (method.name.equals("draw")) {
                AbstractInsnNode showAlertIconTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("isComplete")) {
                        showAlertIconTarget = node;
                    }
                }
                if (showAlertIconTarget != null) {
                    method.instructions.insert(showAlertIconTarget, showAlertIcon());
                    method.instructions.remove(showAlertIconTarget);
                }
            }
            if (method.name.equals("getWidgetType")) {
                AbstractInsnNode isCompleteTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("isComplete")) {
                        isCompleteTarget = node;
                    }
                }
                if (isCompleteTarget != null) {
                    method.instructions.insert(isCompleteTarget, showAlertIcon());
                    method.instructions.remove(isCompleteTarget);
                }
            }
            if (method.name.equals("onClicked")) {
                AbstractInsnNode isCollectibletarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("isComplete")) {
                        isCollectibletarget = node;
                    }
                }
                if (isCollectibletarget != null) {
                    method.instructions.insert(isCollectibletarget, showAlertIcon());
                    method.instructions.remove(isCollectibletarget);
                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList showAlertIcon() {
        InsnList instructions = new InsnList();

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "isQuestRewardAvailable", "(Lcom/feed_the_beast/ftbquests/quest/Quest;Lcom/feed_the_beast/ftbquests/quest/QuestData;)Z", false));

        return instructions;
    }
}
