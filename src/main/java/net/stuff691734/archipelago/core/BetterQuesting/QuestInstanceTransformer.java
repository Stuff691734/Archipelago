package net.stuff691734.archipelago.core.BetterQuesting;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class QuestInstanceTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        if (
                transformedName.equals("betterquesting.client.importers.ImportedQuests") ||
                transformedName.equals("betterquesting.legacy.v0.LegacyLoader_v0") ||
                transformedName.equals("betterquesting.questing.QuestDatabase")
        ) {
            return transformClass(basicClass);
        }
        return basicClass;
    }

    public byte[] transformClass(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        classNode.methods.forEach((method) -> {
            if (
                    method.name.equals("createNew") || // QuestDatabase + ImportedQuests
                    method.name.equals("readQuestDatabase") // LegacyLoader_v0
            ) {
                // modifying inline since not adding or subtracting nodes
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    // replace `new QuestInstance()` with `new ArchipelagoQuestInstance()`
                    if (node.getOpcode() == NEW && ((TypeInsnNode)node).desc.equals("betterquesting/questing/QuestInstance")) {
                        ((TypeInsnNode)node).desc = "net/stuff691734/archipelago/betterquesting/implementations/ArchipelagoQuestInstance";
                    }
                    if (node.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)node).owner.equals("betterquesting/questing/QuestInstance")) {
                        ((MethodInsnNode)node).owner = "net/stuff691734/archipelago/betterquesting/implementations/ArchipelagoQuestInstance";
                    }
                }
            }
        });

        // cleanup
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
