package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class GuiScreenAdvancementsTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("net.minecraft.client.gui.advancements.GuiScreenAdvancements")) {
            // not the class we are looking for, no changes.
            return basicClass;
        }
        return transformClass(basicClass);
    }

    private byte[] transformClass(byte[] basicClass) {
        // setup
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        // find method
        classNode.methods.forEach((method) -> {
            // GuiScreenAdvancements.rootAdvancementAdded
            if (method.name.equals("func_191931_a")) {
                AbstractInsnNode showAdvancementPageTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // AdvancementTabGui.create
                    if (node.getOpcode() == INVOKESTATIC && ((MethodInsnNode)node).name.equals("func_193936_a")) {
                        showAdvancementPageTarget = node;
                    }
                }
                if (showAdvancementPageTarget != null) {
                    method.instructions.insert(showAdvancementPageTarget, showAdvancementPage());
                }
            }

        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static InsnList showAdvancementPage() {
        InsnList instructions = new InsnList();

        instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));

        instructions.add(new InsnNode(SWAP));

        instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new VarInsnNode(ALOAD, 1));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));


        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "isTabDrawn", "(Ljava/lang/Object;Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Ljava/lang/Object;", false));
        instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/gui/advancements/GuiAdvancementTab"));

        return instructions;

    }
}
