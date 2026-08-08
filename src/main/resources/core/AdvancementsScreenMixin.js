var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

var GETSTATIC = Opcodes.GETSTATIC;
var SWAP = Opcodes.SWAP;
var NEW = Opcodes.NEW;
var DUP = Opcodes.DUP;
var ALOAD = Opcodes.ALOAD;
var INVOKESPECIAL = Opcodes.INVOKESPECIAL;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var CHECKCAST = Opcodes.CHECKCAST;
var INVOKESTATIC = Opcodes.INVOKESTATIC;

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

                            // AdvancementTabGui.create
                            if (node.getOpcode() === INVOKESTATIC && node.name.equals(ASMAPI.mapMethod("func_193936_a"))) {
                                avoidAddingEmptyPagesTarget = node;
                            }
                        }
                        if (avoidAddingEmptyPagesTarget != null) {
                            method.instructions.insert(avoidAddingEmptyPagesTarget, showAdvancementPage());

                        }
                    }

                });

                return classNode;
            }
        }
    }
}

function showAdvancementPage() {
    var instructions = new InsnList();

    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));

    instructions.add(new InsnNode(SWAP));

    instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
    instructions.add(new InsnNode(DUP));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));


    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "isTabDrawn", "(Ljava/lang/Object;Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Ljava/lang/Object;", false));
    instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/gui/advancements/AdvancementTabGui"));

    return instructions;
}