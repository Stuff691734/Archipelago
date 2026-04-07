package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class GuiAdvancementTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("net.minecraft.client.gui.advancements.GuiAdvancement")) {
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
            // AdvancementsEntryGui.draw
            if (method.name.equals("func_191817_b")) {
                AbstractInsnNode drawSetHiddenTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // DisplayInfo.isHidden
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("func_193224_j")) {
                        drawSetHiddenTarget = node;
                    }
                }
                if (drawSetHiddenTarget != null) {
                    method.instructions.insert(drawSetHiddenTarget, SetHidden());
                    method.instructions.remove(drawSetHiddenTarget);
                }
            }

            // AdvancmentsEntryGui.isMouseOver
            if (method.name.equals("func_191816_c")) {
                AbstractInsnNode isMouseOverSetHiddenTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // DisplayInfo.isHidden
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode)node).name.equals("func_193224_j")) {
                        isMouseOverSetHiddenTarget = node;
                    }
                }
                if (isMouseOverSetHiddenTarget != null) {
                    method.instructions.insert(isMouseOverSetHiddenTarget, SetHidden());
                    method.instructions.remove(isMouseOverSetHiddenTarget);
                }
            }

            // AdvancmentsEntryGui.drawConnectivity
            if (method.name.equals("func_191819_a")) {
                AbstractInsnNode drawConnectivityTarget = null;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // DisplayInfo.isHidden
                    // only for first one
                    if (drawConnectivityTarget == null && node.getOpcode() == GETFIELD && ((FieldInsnNode)node).name.equals("field_191834_l")) {
                        drawConnectivityTarget = node;
                    }
                }
                if (drawConnectivityTarget != null) {
                    method.instructions.insert(drawConnectivityTarget, DrawConnectivitySetHidden());

                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public InsnList SetHidden() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "field_191829_g", "Lnet/minecraft/advancements/Advancement;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "shouldAdvancementBeHidden", "(Lnet/minecraft/advancements/DisplayInfo;Lnet/minecraft/advancements/Advancement;)Z", false));

        return instructions;
    }

    public InsnList DrawConnectivitySetHidden() {
        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "field_191830_h", "Lnet/minecraft/advancements/DisplayInfo;"));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "field_191829_g", "Lnet/minecraft/advancements/Advancement;"));
        instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "getGuiAdvancementParent", "(Lnet/minecraft/client/gui/advancements/GuiAdvancement;Lnet/minecraft/advancements/DisplayInfo;Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/client/gui/advancements/GuiAdvancement;", false));

        return instructions;
    }
}
