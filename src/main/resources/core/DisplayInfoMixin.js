var Opcodes = Java.type('org.objectweb.asm.Opcodes');


var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');

var ALOAD = Opcodes.ALOAD;
var GETFIELD = Opcodes.GETFIELD;
var ARETURN = Opcodes.ARETURN;
var ACC_PUBLIC = Opcodes.ACC_PUBLIC;

function initializeCoreMod() {
    return {
        "archipelago$AdvancementsScreenMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/advancements/DisplayInfo"
            },
            "transformer": function(classNode) {
                classNode.methods.add(archipelago$getIcon())

                return classNode;
            }
        }
    }
}

function archipelago$getIcon() {
    var method = new MethodNode(ACC_PUBLIC, "archipelago$getIcon", "()Lnet/minecraft/item/ItemStack;", null, null);

    var instructions = new InsnList();

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/advancements/DisplayInfo", "icon", "Lnet/minecraft/item/ItemStack;"));
    instructions.add(new InsnNode(ARETURN));

    method.instructions.add(instructions);

    return method;
}