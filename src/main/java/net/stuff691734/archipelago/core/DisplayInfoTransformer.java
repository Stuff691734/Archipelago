package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class DisplayInfoTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("net.minecraft.advancements.DisplayInfo")) {
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

        classNode.methods.add(archipelago$getIcon());

        classNode.interfaces.add("net/stuff691734/archipelago/mixin/DisplayInfoAccessor");

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public MethodNode archipelago$getIcon() {
        MethodNode method = new MethodNode(ACC_PUBLIC, "archipelago$getIcon", "()Lnet/minecraft/item/ItemStack;", null, null);

        InsnList instructions = new InsnList();

        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/advancements/DisplayInfo", "field_192301_b", "Lnet/minecraft/item/ItemStack;"));
        instructions.add(new InsnNode(ARETURN));

        method.instructions.add(instructions);

        return method;
    }
}
