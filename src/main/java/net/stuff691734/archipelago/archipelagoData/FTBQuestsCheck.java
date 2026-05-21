package net.stuff691734.archipelago.archipelagoData;

public class FTBQuestsCheck extends Check implements Comparable<FTBQuestsCheck> {

    public FTBQuestsCheck(String type, DependencyNotation dependencies, String chapter) {
        super(type, dependencies, chapter);
    }

    @Override
    public int compareTo(FTBQuestsCheck ftbQuestsCheck) {
        return this.page.compareTo(ftbQuestsCheck.page);
    }
}
