package features;

import java.util.Objects;

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

    public String getFullName() {
        return name + " (" + international + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return name.equals(currency.name) && international.equals(currency.international);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, international);
    }
}
