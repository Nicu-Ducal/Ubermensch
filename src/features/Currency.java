package features;

import java.util.Objects;

public class Currency {
    private Integer id;
    private final String name;
    private final String international;

    public Currency(Integer id, String name, String international) {
        this.id = id;
        this.name = name;
        this.international = international;
    }

    public Integer getID() { return id; }

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
