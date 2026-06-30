package net.stuff691734.archipelago.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class JsonToNBTTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !transformedName.equals("net.minecraft.nbt.JsonToNBT")) {
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

        classNode.methods.add(archipelago$readStruct());

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public MethodNode archipelago$readStruct() {
        MethodNode method = new MethodNode(ACC_PUBLIC | ACC_STATIC, "archipelago$readStruct", "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;", null, null);

        InsnList instructions = new InsnList();

        instructions.add(new TypeInsnNode(NEW, "net/minecraft/nbt/JsonToNBT"));
        instructions.add(new InsnNode(DUP));
        instructions.add(new VarInsnNode(ALOAD, 0));
        instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/nbt/JsonToNBT", "<init>", "(Ljava/lang/String;)V", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/nbt/JsonToNBT", "func_193593_f", "()Lnet/minecraft/nbt/NBTTagCompound;", false));
        instructions.add(new InsnNode(ARETURN));

        method.instructions.add(instructions);

        return method;
    }
}
