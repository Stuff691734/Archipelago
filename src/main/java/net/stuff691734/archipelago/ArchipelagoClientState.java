package net.stuff691734.archipelago;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArchipelagoClientState {
    private final Set<String> checks;

    public ArchipelagoClientState() {
        this.checks = new HashSet<>();
    }

    public boolean hasCheck(String checkName) {
        return this.checks.contains(checkName);
    }

    public void addCheck(String checkName) {
        this.checks.add(checkName);
    }

    public void addAllChecks(String[] checks) {
        this.checks.addAll(Arrays.asList(checks));
    }

    public void clear() {
        this.checks.clear();
    }
}
