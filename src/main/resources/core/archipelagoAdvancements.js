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
var AbstractInsnNode = Java.type('org.objectweb.asm.tree.AbstractInsnNode')

var ALOAD = Opcodes.ALOAD;
var INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
var IFNULL = Opcodes.IFNULL;
var IFEQ = Opcodes.IFEQ;
var GETSTATIC = Opcodes.GETSTATIC;
var GETFIELD = Opcodes.GETFIELD;
var INVOKEINTERFACE = Opcodes.INVOKEINTERFACE;
var CHECKCAST = Opcodes.CHECKCAST;
var ASTORE = Opcodes.ASTORE;
var POP = Opcodes.POP;
var INVOKESTATIC = Opcodes.INVOKESTATIC;
var ICONST_0 = Opcodes.ICONST_0;
var IRETURN = Opcodes.IRETURN;
var IFNE = Opcodes.IFNE;
var IF_ACMPNE = Opcodes.IF_ACMPNE;
var GOTO = Opcodes.GOTO;

function initializeCoreMod() {
    return {
        "archipelagoCore": {
            "target": {
                "type": "CLASS",
                "name": "net/minecraft/advancements/PlayerAdvancements"
            },
            "transformer": function(classNode) {
                classNode.methods.forEach(function (method) {
                	if (method.name.equals(ASMAPI.mapMethod("func_192750_a"))) {
                	    var sendArchipelagoAdvancementTarget = null;
                	    for (var iterator = method.instructions.iterator(); iterator.hasNext();) {
                            // I am only doing preventAdvancement in place because I can't figure out how to loop over it.
                            // regardless sendArchipelagoAdvancement will only be run on the last instance of IRETURN
                            var node = iterator.next();
                            if (node.getOpcode() === IRETURN) {
                                sendArchipelagoAdvancementTarget = node;
                            }
                            if (node.getOpcode() === INVOKEVIRTUAL
                                    && node.name.equals(ASMAPI.mapMethod("func_192105_a"))) {
                                method.instructions.insert(node, preventAdvancement());
                            }
                        }
                        if (sendArchipelagoAdvancement !== null) {
                            method.instructions.insertBefore(sendArchipelagoAdvancementTarget, sendArchipelagoAdvancement());
                        }
                	}
                });
                return classNode;
            }
        }
    }
}

function sendArchipelagoAdvancement() {
    var newInstructionList = new InsnList();
    var L1 = new LabelNode();
    // L0
    var L0 = new LabelNode();
    newInstructionList.add(L0);
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192068_c", "()Lnet/minecraft/advancements/DisplayInfo;", false));
    newInstructionList.add(new JumpInsnNode(IFNULL, L1));

    // L2
    var L2 = new LabelNode();
    newInstructionList.add(L2);
    newInstructionList.add(new VarInsnNode(ALOAD, 4));
    newInstructionList.add(preventAdvancement());
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/AdvancementProgress", "func_192105_a", "()Z", false));
    newInstructionList.add(new JumpInsnNode(IFEQ, L1));

    // L3
    var L3 = new LabelNode();
    newInstructionList.add(L3);
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "isConnected", "()Z", false));
    newInstructionList.add(new JumpInsnNode(IFEQ, L1));

    // L4
    var L4 = new LabelNode();
    newInstructionList.add(L4);
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "getDataPackage", "()Lshadow/archipelago/io/github/archipelagomw/parts/DataPackage;", false));
    newInstructionList.add(new LdcInsnNode("Modded Minecraft"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "shadow/archipelago/io/github/archipelagomw/parts/DataPackage", "getGame", "(Ljava/lang/String;)Lshadow/archipelago/io/github/archipelagomw/parts/Game;", false));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "shadow/archipelago/io/github/archipelagomw/parts/Game", "locationNameToId", "Ljava/util/Map;"));
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Long"));
    newInstructionList.add(new VarInsnNode(ASTORE, 6));

    // L5
    var L5 = new LabelNode();
    newInstructionList.add(L5);
    newInstructionList.add(new VarInsnNode(ALOAD, 6));
    newInstructionList.add(new JumpInsnNode(IFNULL, L1));

    // L6
    var L6 = new LabelNode();
    newInstructionList.add(L6);
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "getLocationManager", "()Lshadow/archipelago/io/github/archipelagomw/LocationManager;", false));
    newInstructionList.add(new VarInsnNode(ALOAD, 6));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "shadow/archipelago/io/github/archipelagomw/LocationManager", "checkLocation", "(J)Lshadow/archipelago/io/github/archipelagomw/APResult;", false));
    newInstructionList.add(new InsnNode(POP));

    // L7
    var L7 = new LabelNode();
    newInstructionList.add(L7);
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/ChecksState", "getServerState", "(Lnet/minecraft/server/MinecraftServer;)Lnet/stuff691734/archipelago/ChecksState;", false));
    newInstructionList.add(new VarInsnNode(ASTORE, 7));

    // L8
    var L8 = new LabelNode();
    newInstructionList.add(L8);
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
    newInstructionList.add(new LdcInsnNode("final_goal"));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
    newInstructionList.add(new JumpInsnNode(IFEQ, L1));

    // L9
    var L9 = new LabelNode();
    newInstructionList.add(L9);
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "client", "Lnet/stuff691734/archipelago/ArchipelagoClient;"));
    newInstructionList.add(new FieldInsnNode(GETSTATIC, "shadow/archipelago/io/github/archipelagomw/ClientStatus", "CLIENT_GOAL", "Lshadow/archipelago/io/github/archipelagomw/ClientStatus;"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/stuff691734/archipelago/ArchipelagoClient", "setGameState", "(Lshadow/archipelago/io/github/archipelagomw/ClientStatus;)Lshadow/archipelago/io/github/archipelagomw/APResult;", false));
    newInstructionList.add(new InsnNode(POP));

    // L1
    newInstructionList.add(L1);

    return newInstructionList;
}

function preventAdvancement() {
    var newInstructionList = new InsnList();
    var L1 = new LabelNode();

    // L0
    var L0 = new LabelNode();

    newInstructionList.add(L0);
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192068_c", "()Lnet/minecraft/advancements/DisplayInfo;", false));
    newInstructionList.add(new JumpInsnNode(IFNULL, L1));

    // L2
    var L2 = new LabelNode();
    newInstructionList.add(L2);

    newInstructionList.add(new FieldInsnNode(GETSTATIC, "net/stuff691734/archipelago/Archipelago", "server", "Lnet/minecraft/server/MinecraftServer;"));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/ChecksState", "getServerState", "(Lnet/minecraft/server/MinecraftServer;)Lnet/stuff691734/archipelago/ChecksState;", false));
    // gonna be consistent with where I store variables
    newInstructionList.add(new VarInsnNode(ASTORE, 7));

    var L4 = new LabelNode();

    // L3
    var L3 = new LabelNode();
    newInstructionList.add(L3);
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
    newInstructionList.add(new LdcInsnNode("unlock_type"));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new LdcInsnNode("tab"));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
    newInstructionList.add(new JumpInsnNode(IFEQ, L4));

    // L5
    var L5 = new LabelNode();
    newInstructionList.add(L5);
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "getRoot", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/Advancement;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
    newInstructionList.add(new JumpInsnNode(IFNE, L1));

    // L6
    var L6 = new LabelNode();
    newInstructionList.add(L6);
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new InsnNode(IRETURN));

    var L7 = new LabelNode();

    // L4
    newInstructionList.add(L4);
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "slotData", "Ljava/util/Map;"));
    newInstructionList.add(new LdcInsnNode("unlock_type"));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new LdcInsnNode("tree"));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/util/Objects", "equals", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false));
    newInstructionList.add(new JumpInsnNode(IFEQ, L7));

    var L9 = new LabelNode();

    // L8
    var L8 = new LabelNode();
    newInstructionList.add(L8);
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "net/stuff691734/archipelago/Utils", "getRoot", "(Lnet/minecraft/advancements/Advancement;)Lnet/minecraft/advancements/Advancement;", false));
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new JumpInsnNode(IF_ACMPNE, L9));

    // L10
    var L10 = new LabelNode();
    newInstructionList.add(L10);
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
    newInstructionList.add(new JumpInsnNode(IFNE, L1));

    // L11
    var L11 = new LabelNode();
    newInstructionList.add(L11);
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new InsnNode(IRETURN));

    // L9
    newInstructionList.add(L9);
    newInstructionList.add(new VarInsnNode(ALOAD, 1));
    newInstructionList.add(new VarInsnNode(ASTORE, 8));

    var L13 = new LabelNode();

    // L12
    var L12 = new LabelNode();
    newInstructionList.add(L12);
    newInstructionList.add(new VarInsnNode(ALOAD, 8));
    newInstructionList.add(new JumpInsnNode(IFNULL, L13));

    // L14
    var L14 = new LabelNode();
    newInstructionList.add(L14);
    newInstructionList.add(new VarInsnNode(ALOAD, 8));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192070_b", "()Lnet/minecraft/advancements/Advancement;", false));
    newInstructionList.add(new VarInsnNode(ASTORE, 8));

    // L15
    var L15 = new LabelNode();
    newInstructionList.add(L15);
    newInstructionList.add(new VarInsnNode(ALOAD, 8));
    newInstructionList.add(new JumpInsnNode(IFNULL, L12));

    // L16
    var L16 = new LabelNode();
    newInstructionList.add(L16);
    newInstructionList.add(new VarInsnNode(ALOAD, 7));
    newInstructionList.add(new FieldInsnNode(GETFIELD, "net/stuff691734/archipelago/ChecksState", "checks", "Ljava/util/Map;"));
    newInstructionList.add(new VarInsnNode(ALOAD, 8));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/advancements/Advancement", "func_192067_g", "()Lnet/minecraft/util/ResourceLocation;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false));
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
    newInstructionList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true));
    newInstructionList.add(new TypeInsnNode(CHECKCAST, "java/lang/Boolean"));
    newInstructionList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
    newInstructionList.add(new JumpInsnNode(IFNE, L12));

    // L17
    var L17 = new LabelNode();
    newInstructionList.add(L17);
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new InsnNode(IRETURN));

    // L13
    newInstructionList.add(L13);
    newInstructionList.add(new JumpInsnNode(GOTO, L1));

    // L7
    newInstructionList.add(L7);

    // L18
    var L18 = new LabelNode();
    newInstructionList.add(L18);
    newInstructionList.add(new InsnNode(ICONST_0));
    newInstructionList.add(new InsnNode(IRETURN));

    // L1
    newInstructionList.add(L1);

    return newInstructionList;
}