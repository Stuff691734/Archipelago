package net.stuff691734.archipelago.core.FTBQuests;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class PanelViewQuestTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("com.feed_the_beast.ftbquests.gui.tree.PanelViewQuest")) {
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
            if (method.name.equals("addWidgets")) {
                AbstractInsnNode isEmptyNode = null;
                int isEmptyIndex = 0;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode)node).name.equals("isEmpty")) {
                        isEmptyIndex++;
                        if (isEmptyIndex == 3) {
                            isEmptyNode = node;
                        }
                    }
                }
                if (isEmptyNode != null) {
                    method.instructions.insert(isEmptyNode, alwaysHaveDependencies());
                    method.instructions.remove(isEmptyNode);
                }

            }

            if (method.name.equals("showList")) {
                AbstractInsnNode openContextMenuNode = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("openContextMenu")) {
                        openContextMenuNode = node;
                    }
                }

                if (openContextMenuNode != null) {
                    method.instructions.insertBefore(openContextMenuNode, addArchipelagoDependency());
                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList alwaysHaveDependencies() {
        InsnList instructions = new InsnList();

        instructions.add(new InsnNode(POP));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/gui/tree/PanelViewQuest", "quest", "Lcom/feed_the_beast/ftbquests/quest/Quest;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "alwaysHaveDependencies", "(Lcom/feed_the_beast/ftbquests/quest/Quest;)Z", false));

        return instructions;
    }

    public InsnList addArchipelagoDependency() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/gui/tree/PanelViewQuest", "quest", "Lcom/feed_the_beast/ftbquests/quest/Quest;"));
        instructions.add(new VarInsnNode(ALOAD, 3));
        instructions.add(new VarInsnNode(ALOAD, 1));

        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "addArchipelagoDependency", "(Lcom/feed_the_beast/ftbquests/quest/Quest;Ljava/util/List;Ljava/util/Collection;)V", false));

        return instructions;
    }
}
