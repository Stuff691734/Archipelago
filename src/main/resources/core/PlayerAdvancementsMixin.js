var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var Handle = Java.type('org.objectweb.asm.Handle');
var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Type = Java.type('org.objectweb.asm.Type');

var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var InvokeDynamicInsnNode = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var IFEQ = Opcodes.IFEQ;
var GETSTATIC = Opcodes.GETSTATIC;
var GETFIELD = Opcodes.GETFIELD;
var INVOKEINTERFACE = Opcodes.INVOKEINTERFACE;
var CHECKCAST = Opcodes.CHECKCAST;
var ASTORE = Opcodes.ASTORE;
var POP = Opcodes.POP;
var INVOKESTATIC = Opcodes.INVOKESTATIC;
var ICONST_0 = Opcodes.ICONST_0;
var ICONST_1 = Opcodes.ICONST_1;
var IRETURN = Opcodes.IRETURN;
var RETURN = Opcodes.RETURN;
var IFNE = Opcodes.IFNE;
var GOTO = Opcodes.GOTO;
var INVOKESPECIAL = Opcodes.INVOKESPECIAL;
var NEW = Opcodes.NEW;
var DUP = Opcodes.DUP;
var H_INVOKESTATIC = Opcodes.H_INVOKESTATIC;
var ACC_PRIVATE = Opcodes.ACC_PRIVATE;
var ACC_SYNTHETIC = Opcodes.ACC_SYNTHETIC;
var ACC_STATIC = Opcodes.ACC_STATIC;
var ACC_PUBLIC = Opcodes.ACC_PUBLIC;

function initializeCoreMod() {
    return {
        "archipelago$PlayerAdvancementsMixin": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/advancements/PlayerAdvancements"
            },
            "transformer": function(classNode) {
                classNode.methods.forEach(function (method) {
                    // PlayerAdvancements.load
                	if (method.name.equals(ASMAPI.mapMethod("func_192740_f"))) {
                	    var showAllAdvancementsTarget = null;
                	    var loadAllAdvancementsTarget = null;
                	    for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();
                            // PlayerAdvancement.startProgress
                            if (node.getOpcode() === INVOKESPECIAL && node.name.equals(ASMAPI.mapMethod("func_192743_a"))) {
                                showAllAdvancementsTarget = node;
                            }

                            if (node.getOpcode() === INVOKEINTERFACE && node.name.equals("collect")) {
                                loadAllAdvancementsTarget = node;
                            }
                	    }
                	    if (showAllAdvancementsTarget != null) {
                	        method.instructions.insert(showAllAdvancementsTarget, showAllAdvancements());
                	    }
                	    if (loadAllAdvancementsTarget != null) {
                	        method.instructions.remove(loadAllAdvancementsTarget.previous.previous);
                	        method.instructions.remove(loadAllAdvancementsTarget.previous);
                	        method.instructions.insert(loadAllAdvancementsTarget, loadAllAdvancements());
                	        method.instructions.remove(loadAllAdvancementsTarget);
                	    }
                	}
                    // PlayerAdvancements.shouldBeVisible
                    if (method.name.equals(ASMAPI.mapMethod("func_192738_c"))) {
                        // inserts at start of instructions
                        method.instructions.insert(shouldBeVisible());
                    }
                    // PlayerAdvancements.grantCriterion
                    if (method.name.equals(ASMAPI.mapMethod("func_192750_a"))) {
                        var sendArchipelagoAdvancementTarget = null;
                        for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            var node = iterator.next();

                            if (node.getOpcode() === IRETURN) {
                                sendArchipelagoAdvancementTarget = node;
                            }

                            // AdvancementProgress.isDone
                            if (node.getOpcode() === INVOKEVIRTUAL && node.name.equals(ASMAPI.mapMethod("func_192105_a"))) {
                                method.instructions.insert(node, preventAdvancement());
                            }
                        }
                        if (sendArchipelagoAdvancementTarget != null) {
                            method.instructions.insertBefore(sendArchipelagoAdvancementTarget, sendArchipelagoAdvancement());
                        }
                    }
                });

                classNode.methods.add(lambda$forEach());

                classNode.methods.add(archipelago$ensureVisibility());
                classNode.interfaces.add("net/stuff691734/archipelago/mixin/PlayerAdvancementAccessor");

                return classNode;
            }
        }
    }
}

function lambda$forEach() {
    var method = new MethodNode(ACC_PRIVATE | ACC_SYNTHETIC | ACC_STATIC, "lambda$forEach", "(Ljava/util/Map;Ljava/util/Map$Entry;)V", null, null);

    var instructions = new InsnList();
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/advancements/AdvancementProgress"));
    instructions.add(new InsnNode(POP));
    instructions.add(new InsnNode(RETURN));

    method.instructions.add(instructions);

    return method;
}

function showAllAdvancements() {
    var instructions = new InsnList();

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new FieldInsnNode(GETFIELD, "net/minecraft/advancements/PlayerAdvancements", "progressChanged", "Ljava/util/Set;"));
    instructions.add(new VarInsnNode(ALOAD, 8));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true));
    instructions.add(new InsnNode(POP));

    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new VarInsnNode(ALOAD, 8));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "ensureVisibility", "(Lnet/minecraft/advancements/Advancement;)V", false));

    return instructions;
}

function loadAllAdvancements() {
    var instructions = new InsnList();

    instructions.add(new TypeInsnNode(NEW, "java/util/HashMap"));
    instructions.add(new InsnNode(DUP));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false));
    instructions.add(new VarInsnNode(ASTORE, 3));

    instructions.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", "getAdvancementManager", "()Lnet/minecraft/advancements/AdvancementManager;", false));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementManager", "getAllAdvancements", "()Ljava/util/Collection;", false));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true));
    instructions.add(new VarInsnNode(ASTORE, 4));

    var afterLoop = new LabelNode();

    var startLoop = new LabelNode();
    instructions.add(startLoop);
    instructions.add(new VarInsnNode(ALOAD, 4));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true));
    instructions.add(new JumpInsnNode(IFEQ, afterLoop));
    instructions.add(new VarInsnNode(ALOAD, 4));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true));
    instructions.add(new TypeInsnNode(CHECKCAST, "net/minecraft/advancements/Advancement"));
    instructions.add(new VarInsnNode(ASTORE, 6));

    instructions.add(new VarInsnNode(ALOAD, 3));
    instructions.add(new VarInsnNode(ALOAD, 6));
    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "getId", "()Lnet/minecraft/util/ResourceLocation;", false));
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new VarInsnNode(ALOAD, 6));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "getProgress", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/AdvancementProgress;", false));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    instructions.add(new InsnNode(POP));

    instructions.add(new JumpInsnNode(GOTO, startLoop));

    instructions.add(afterLoop);

    instructions.add(new VarInsnNode(ALOAD, 5));
    instructions.add(new VarInsnNode(ALOAD, 3));
    instructions.add(new InvokeDynamicInsnNode(
        "accept",
        "(Ljava/util/Map;)Ljava/util/function/Consumer;",
        new Handle(H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
        Type.getType("(Ljava/lang/Object;)V"),
        new Handle(H_INVOKESTATIC, "net/minecraft/advancements/PlayerAdvancements", "lambda$forEach", "(Ljava/util/Map;Ljava/util/Map$Entry;)V", false),
        Type.getType("(Ljava/util/Map$Entry;)V")
    ));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/stream/Stream", "forEach", "(Ljava/util/function/Consumer;)V", true));

    instructions.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
    instructions.add(new InsnNode(DUP));
    instructions.add(new VarInsnNode(ALOAD, 3));
    instructions.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;", true));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V", false));

    return instructions;
}

function shouldBeVisible() {
    var instructions = new InsnList();

    var continueExecution = new LabelNode();

    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "shouldBeVisible", "(Lnet/minecraft/advancements/Advancement;)Z", false));
    instructions.add(new JumpInsnNode(IFEQ, continueExecution));

    instructions.add(new InsnNode(ICONST_1));
    instructions.add(new InsnNode(IRETURN));

    instructions.add(continueExecution);

    return instructions;
}

function sendArchipelagoAdvancement() {
    var instructions = new InsnList();

    var continueExecution = new LabelNode();

    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "preventAdvancement", "(Lnet/minecraft/advancements/Advancement;)Z", false));
    instructions.add(new JumpInsnNode(IFEQ, continueExecution));

    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "sendArchipelagoAdvancement", "(Lnet/minecraft/advancements/Advancement;)V", false));

    instructions.add(continueExecution);

    return instructions;
}

function preventAdvancement() {
    var instructions = new InsnList();

    var continueExecution = new LabelNode();

    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/mixin/MixinHelper", "preventAdvancement", "(Lnet/minecraft/advancements/Advancement;)Z", false));
    instructions.add(new JumpInsnNode(IFNE, continueExecution));

    instructions.add(new InsnNode(ICONST_0));
    instructions.add(new InsnNode(IRETURN));

    instructions.add(continueExecution);

    return instructions;
}

function archipelago$ensureVisibility() {
    var method = new MethodNode(ACC_PUBLIC, "archipelago$ensureVisibility", "(Lnet/minecraft/advancements/Advancement;)V", null, null);

    var instructions = new InsnList();
    instructions.add(new VarInsnNode(ALOAD, 0));
    instructions.add(new VarInsnNode(ALOAD, 1));
    instructions.add(new MethodInsnNode(INVOKESPECIAL, "net/minecraft/advancements/PlayerAdvancements", "ensureVisibility", "(Lnet/minecraft/advancements/Advancement;)V", false));
    instructions.add(new InsnNode(RETURN));

    method.instructions.add(instructions);

    return method;
}