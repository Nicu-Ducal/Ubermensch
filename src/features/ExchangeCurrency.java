package features;

import java.util.Objects;

public class ExchangeCurrency {
    private Currency firstCurrency;
    private Currency secondCurrency;

    public ExchangeCurrency(Currency firstCurrency, Currency secondCurrency) {
        this.firstCurrency = firstCurrency;
        this.secondCurrency = secondCurrency;
    }

    public Currency getFirstCurrency() { return firstCurrency; }

    public Currency getSecondCurrency() {
        return secondCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeCurrency)) return false;
        ExchangeCurrency that = (ExchangeCurrency) o;
        return firstCurrency.equals(that.firstCurrency) && secondCurrency.equals(that.secondCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstCurrency, secondCurrency);
    }
}
