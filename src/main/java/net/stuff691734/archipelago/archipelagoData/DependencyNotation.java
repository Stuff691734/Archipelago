package net.stuff691734.archipelago.archipelagoData;

import java.util.ArrayList;

public class DependencyNotation {
    // all values at same layer are treated the same if no minimum specified assume and
    public int minimum;
    public ArrayList<DependencyNotation> nested;
    public ArrayList<String> checks;

    public DependencyNotation() {
        this.minimum = 0;
        this.nested = new ArrayList<>();
        this.checks = new ArrayList<>();
    }

    public DependencyNotation addCheck(String check) {
        this.checks.add(check);
        return this;
    }

    public DependencyNotation addNested(DependencyNotation dependency) {
        this.nested.add(dependency);
        return this;
    }

    public DependencyNotation setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }

    public boolean isEmpty() {
        return this.checks.isEmpty() && this.nested.isEmpty();
    }
}
