package features;

public class Currency {
    private final String name;
    private final String international;

    public Currency(String name, String international) {
        this.name = name;
        this.international = international;
    }

    public String getName() {
        return this.name;
    }

    public String getInternational() {
        return this.international;
    }
}
