package net.stuff691734.archipelago.archipelagoData;

public class Check {
    public String page;
    public String type;
    public DependencyNotation dependencies;

    public Check(String type, DependencyNotation dependencies, String page) {
        this.type = type;
        this.dependencies = dependencies;
        this.page = page;
    }
}