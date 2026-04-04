var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var GETFIELD = Opcodes.GETFIELD;
var INVOKESTATIC = Opcodes.INVOKESTATIC;
var ACONST_NULL = Opcodes.ACONST_NULL;
var GOTO = Opcodes.GOTO;
var IFEQ = Opcodes.IFEQ;


function initializeCoreMod() {
    return {
        "archipelago$AdvancementEntryGuiMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/gui/advancements/GuiAdvancement"
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

                    // AdvancmentsEntryGui.isMouseOver
                    if (method.name.equals(ASMAPI.mapMethod("func_191819_a"))) {
                        var drawConnectivityTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();
                            // DisplayInfo.isHidden
                            // only for first one
                            if (drawConnectivityTarget == null && node.getOpcode() === GETFIELD && node.name.equals(ASMAPI.mapField("field_191834_l"))) {
                                drawConnectivityTarget = node;
                            }
                        }
                        if (drawConnectivityTarget != null) {
                            method.instructions.insert(drawConnectivityTarget, DrawConnectivitySetHidden());
                            method.instructions.remove(drawConnectivityTarget);
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

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "advancement", "Lnet/minecraft/advancements/Advancement;"));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "shouldAdvancementBeHidden", "(Lnet/minecraft/advancements/DisplayInfo;Lnet/minecraft/advancements/Advancement;)Z", false));

    return instructions;
}

function DrawConnectivitySetHidden() {
    var instructions = new InsnList();

    var getParent = new LabelNode();
    var end = new LabelNode();

    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "displayInfo", "Lnet/minecraft/advancements/DisplayInfo;"));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "advancement", "Lnet/minecraft/advancements/Advancement;"));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "shouldAdvancementBeHidden", "(Lnet/minecraft/advancements/DisplayInfo;Lnet/minecraft/advancements/Advancement;)Z", false));

    instructions.add(new JumpInsnNode(IFEQ, getParent));
    instructions.add(new InsnNode(ACONST_NULL));

    instructions.add(new JumpInsnNode(GOTO, end));

    instructions.add(getParent);
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "parent", "Lnet/minecraft/client/gui/advancements/GuiAdvancement;"));

    instructions.add(end);






    return instructions;
}