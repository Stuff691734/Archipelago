package net.stuff691734.archipelago.archipelagoData;

import java.util.Objects;

public enum CheckType {
    ADVANCEMENT("adv"),
    FTB_QUEST("ftb"),
    ITEM("item"),
    DEFAULT("N/a");

    private final String prefix;

    CheckType(String prefix) {
        this.prefix = prefix;
    }

    public String addPrefix(String text) {
        return this.prefix + " " + text;
    }

    public static CheckType getCheckType(String prefix) {
        for (CheckType type : CheckType.values()) {
            if (Objects.equals(type.prefix, prefix)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
