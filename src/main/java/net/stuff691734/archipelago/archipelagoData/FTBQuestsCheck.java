package net.stuff691734.archipelago.archipelagoData;

public class FTBQuestsCheck extends Check implements Comparable<FTBQuestsCheck> {
    public String dependant_type;
    public String chapter;
    public String[] advancement_dependencies;

    public FTBQuestsCheck(String type, String[] dependants, String dependant_type, String chapter, String[] advancement_dependencies) {
        super(type, dependants);
        this.dependant_type = dependant_type;
        this.chapter = chapter;
        this.advancement_dependencies = advancement_dependencies;
    }

    @Override
    public int compareTo(FTBQuestsCheck ftbQuestsCheck) {
        return this.chapter.compareTo(ftbQuestsCheck.chapter);
    }
}
