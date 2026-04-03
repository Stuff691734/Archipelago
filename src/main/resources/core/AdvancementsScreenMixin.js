var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var IFEQ = Opcodes.IFEQ;
var GETSTATIC = Opcodes.GETSTATIC;
var GETFIELD = Opcodes.GETFIELD;
var INVOKEINTERFACE = Opcodes.INVOKEINTERFACE;
var CHECKCAST = Opcodes.CHECKCAST;
var INVOKESTATIC = Opcodes.INVOKESTATIC;
var ICONST_0 = Opcodes.ICONST_0;
var POP = Opcodes.POP;

function initializeCoreMod() {
    return {
        "archipelago$AdvancementsScreenMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/gui/advancements/AdvancementsScreen"
            },
            "transformer": function(classNode) {
                classNode.methods.forEach(function (method) {
                    // AdvancementsScreen.rootAdvancementAdded
                    if (method.name.equals(ASMAPI.mapMethod("func_191931_a"))) {
                        var avoidAddingEmptyPagesTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();

                            if (node.getOpcode() === INVOKEINTERFACE && node.name.equals("put")) {
                                avoidAddingEmptyPagesTarget = node;
                            }
                        }
                        if (avoidAddingEmptyPagesTarget != null) {
                            method.instructions.remove(avoidAddingEmptyPagesTarget.previous);
                            method.instructions.remove(avoidAddingEmptyPagesTarget.previous);
                            method.instructions.remove(avoidAddingEmptyPagesTarget.previous);
                            method.instructions.remove(avoidAddingEmptyPagesTarget.previous);
                            method.instructions.remove(avoidAddingEmptyPagesTarget.next)
                            method.instructions.insert(avoidAddingEmptyPagesTarget, avoidAddingEmptyPages());
                            method.instructions.remove(avoidAddingEmptyPagesTarget);

                        }
                    }

                });

                return classNode;
            }
        }
    }
}

function avoidAddingEmptyPages() {
    var instructions = new InsnList();

    var innerIf = new LabelNode();

    var end = new LabelNode();

    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "slotData", "Lnet/stuff691734/archipelago/SlotData;"));
    instructions.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/SlotData", "isInitiated", "Z"));
    instructions.add(new JumpInsnNode(IFEQ, innerIf));
    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "slotData", "Lnet/stuff691734/archipelago/SlotData;"));
    instructions.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/SlotData", "activated_modules", "Ljava/util/List;"));
    instructions.add(new LdcInsnNode("Advancements"));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true));
    instructions.add(new JumpInsnNode(IFEQ, end));

    instructions.add(innerIf);
    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "archipelagoPersistentState", "Lnet/stuff691734/archipelago/ArchipelagoPersistentState;"));
    instructions.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ArchipelagoPersistentState", "advancementChecks", "Ljava/util/Map;"));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "getId", "()Lnet/minecraft/util/ResourceLocation;", false));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    instructions.add(new InsnNode(ICONST_0));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    instructions.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
    instructions.add(new JumpInsnNode(IFEQ, end));

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/AdvancementsScreen", "tabs", "Ljava/util/Map;"));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new VarInsnNode(ALOAD, 2));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
    instructions.add(new InsnNode(POP));

    instructions.add(end);

    return instructions;
}