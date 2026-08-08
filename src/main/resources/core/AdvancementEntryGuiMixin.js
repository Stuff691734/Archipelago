var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var GETFIELD = Opcodes.GETFIELD;
var GETSTATIC = Opcodes.GETSTATIC;
var NEW = Opcodes.NEW;
var DUP = Opcodes.DUP;
var INVOKESPECIAL = Opcodes.INVOKESPECIAL;
var GOTO = Opcodes.GOTO;
var IFNE = Opcodes.IFNE;
var ICONST_1 = Opcodes.ICONST_1;
var CHECKCAST = Opcodes.CHECKCAST;
var ICONST_0 = Opcodes.ICONST_0;


function initializeCoreMod() {
    return {
        "archipelago$AdvancementEntryGuiMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/gui/advancements/AdvancementEntryGui"
            },
            "transformer": function(classNode) {
                classNode.methods.forEach(function (method) {
                    // AdvancementsEntryGui.draw
                    if (method.name.equals(ASMAPI.mapMethod("func_191817_b"))) {
                        var drawSetHiddenTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();
                            // DisplayInfo.isHidden
                            if (node.getOpcode() === INVOKEVIRTUAL && node.name.equals(ASMAPI.mapMethod("func_193224_j"))) {
                                drawSetHiddenTarget = node;
                            }
                        }
                        if (drawSetHiddenTarget != null) {
                            method.instructions.insert(drawSetHiddenTarget, SetHidden());
                            method.instructions.remove(drawSetHiddenTarget);
                        }
                    }

                    // AdvancmentsEntryGui.isMouseOver
                    if (method.name.equals(ASMAPI.mapMethod("func_191816_c"))) {
                        var isMouseOverSetHiddenTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();
                            // DisplayInfo.isHidden
                            if (node.getOpcode() === INVOKEVIRTUAL && node.name.equals(ASMAPI.mapMethod("func_193224_j"))) {
                                isMouseOverSetHiddenTarget = node;
                            }
                        }
                        if (isMouseOverSetHiddenTarget != null) {
                            method.instructions.insert(isMouseOverSetHiddenTarget, SetHidden());
                            method.instructions.remove(isMouseOverSetHiddenTarget);
                        }
                    }

                    // AdvancmentsEntryGui.drawConnectivity
                    if (method.name.equals(ASMAPI.mapMethod("func_191819_a"))) {
                        var drawConnectivityTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();
                            // this.parent
                            // only for first one
                            if (drawConnectivityTarget == null && node.getOpcode() === GETFIELD && node.name.equals(ASMAPI.mapField("field_191834_l"))) {
                                drawConnectivityTarget = node;
                            }
                        }
                        if (drawConnectivityTarget != null) {
                            method.instructions.insert(drawConnectivityTarget, DrawConnectivitySetHidden());
                        }
                    }
                });

                return classNode;
            }
        }
    }
}

function SetHidden() {
    var instructions = new InsnList();

    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));
    instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
    instructions.add(new InsnNode(DUP));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/AdvancementEntryGui", "advancement", "Lnet/minecraft/advancements/Advancement;"));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "shouldShowAdvancement", "(Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Z", false));
    var L1 = new LabelNode();
    var L2 = new LabelNode();
    instructions.add(new JumpInsnNode(IFNE, L1));
    instructions.add(new InsnNode(ICONST_1));
    instructions.add(new JumpInsnNode(GOTO, L2));
    instructions.add(L1);
    instructions.add(new InsnNode(ICONST_0));
    instructions.add(L2);
    instructions.add(new InsnNode(ICONST_0));

    return instructions;
}

function DrawConnectivitySetHidden() {
    var instructions = new InsnList();

    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "logic", "Lnet/stuff691734/archipelagoLib/Logic;"));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/AdvancementEntryGui", "parent", "Lnet/minecraft/client/gui/advancements/AdvancementEntryGui;"));
    instructions.add(new TypeInsnNode(NEW, "net/stuff691734/archipelago/implementations/AdvancementImpl"));
    instructions.add(new InsnNode(DUP));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/AdvancementEntryGui", "advancement", "Lnet/minecraft/advancements/Advancement;"));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/stuff691734/archipelago/implementations/AdvancementImpl", "<init>", "(Lnet/minecraft/advancements/Advancement;)V", false));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelagoLib/Logic", "isDependencyDrawn", "(Ljava/lang/Object;Lnet/stuff691734/archipelagoLib/interfaces/AdvancementInterface;)Ljava/lang/Object;", false));
    instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/client/gui/advancements/AdvancementEntryGui"));

    return instructions;
}