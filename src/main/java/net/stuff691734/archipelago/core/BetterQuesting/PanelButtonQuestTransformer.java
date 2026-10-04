package net.stuff691734.archipelago.core.BetterQuesting;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class PanelButtonQuestTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("betterquesting.api2.client.gui.controls.PanelButtonQuest")) {
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

        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList addToTooltip() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 4));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new VarInsnNode(ALOAD, 5));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/BetterQuestingMixinHelper", "addToTooltip", "(Ljava/util/List;Lbetterquesting/api/questing/IQuest;Ljava/util/UUID;)V", false));

        return instructions;
    }
}
