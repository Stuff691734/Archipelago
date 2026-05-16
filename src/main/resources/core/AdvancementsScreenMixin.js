var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

var INVOKESTATIC = Opcodes.INVOKESTATIC;

function initializeCoreMod() {
    return {
        "archipelago$AdvancementsScreenMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/client/gui/advancements/GuiScreenAdvancements"
            },
            "transformer": function(classNode) {
                classNode.methods.forEach(function (method) {
                    // GuiScreenAdvancements.rootAdvancementAdded
                    if (method.name.equals(ASMAPI.mapMethod("func_191931_a"))) {
                        var avoidAddingEmptyPagesTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();

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

    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "getGuiAdvancementTab", "(Lnet/minecraft/client/gui/advancements/GuiAdvancementTab;)Lnet/minecraft/client/gui/advancements/GuiAdvancementTab;", false))

    return instructions;
}