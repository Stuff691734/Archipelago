package net.stuff691734.archipelago.core.FTBQuests;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ButtonQuestTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("com.feed_the_beast.ftbquests.gui.tree.ButtonQuest")) {
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
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("getShape")) {
                        showAlertIconTarget = node;
                    }
                }
                if (showAlertIconTarget != null) {
                    method.instructions.insert(showAlertIconTarget, showAlertIcon());
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

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/gui/tree/ButtonQuest", "quest", "Lcom/feed_the_beast/ftbquests/quest/Quest;"));
        instructions.add(new VarInsnNode(ALOAD, 7));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/gui/tree/ButtonQuest", "treeGui", "Lcom/feed_the_beast/ftbquests/gui/tree/GuiQuestTree;"));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/gui/tree/GuiQuestTree", "file", "Lcom/feed_the_beast/ftbquests/client/ClientQuestFile;"));
        instructions.add(new FieldInsnNode(GETFIELD, "com/feed_the_beast/ftbquests/client/ClientQuestFile", "self", "Lcom/feed_the_beast/ftbquests/client/ClientQuestData;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/FTBQuestsMixinHelper", "getQuestIcon", "(Lcom/feed_the_beast/ftbquests/quest/Quest;Lcom/feed_the_beast/ftblib/lib/icon/Icon;Lcom/feed_the_beast/ftbquests/client/ClientQuestData;)Lcom/feed_the_beast/ftblib/lib/icon/Icon;", false));
        instructions.add(new VarInsnNode(ASTORE, 7));

        return instructions;
    }
}
