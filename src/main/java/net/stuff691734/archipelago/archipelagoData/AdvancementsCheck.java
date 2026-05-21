package net.stuff691734.archipelago.archipelagoData;

public class AdvancementsCheck extends Check{
    public AdvancementsCheck(String type, String parent_id, String root) {
        super(type, new DependencyNotation().addCheck(parent_id), root);
        if (parent_id == null) {
            this.dependencies = new DependencyNotation();
        }
    }
}
