package net.stuff691734.archipelago.archipelagoData;

public class FTBQuestsCheck extends Check {
    public String dependant_type;
    public String chapter;

    public FTBQuestsCheck(String type, String[] dependants, String dependant_type, String chapter) {
        super(type, dependants);
        this.dependant_type = dependant_type;
        this.chapter = chapter;
    }
}
