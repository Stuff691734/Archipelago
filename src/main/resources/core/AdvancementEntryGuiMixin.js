var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var GETFIELD = Opcodes.GETFIELD;
var INVOKESTATIC = Opcodes.INVOKESTATIC;


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

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "displayInfo", "Lnet/minecraft/advancements/DisplayInfo;"));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/gui/advancements/GuiAdvancement", "advancement", "Lnet/minecraft/advancements/Advancement;"));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "getGuiAdvancementParent", "(Lnet/minecraft/client/gui/advancements/GuiAdvancement;Lnet/minecraft/advancements/DisplayInfo;Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/client/gui/advancements/GuiAdvancement;", false));

    return instructions;
}