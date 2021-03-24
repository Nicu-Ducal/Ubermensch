package features;

public class ExchangeCurrency {
    private Currency firstCurrency;
    private Currency secondCurrency;

    public ExchangeCurrency(Currency firstCurrency, Currency secondCurrency, Double exchangeRate) {
        this.firstCurrency = firstCurrency;
        this.secondCurrency = secondCurrency;
    }

    public Currency getFirstCurrency() {
        return firstCurrency;
    }

    public Currency getSecondCurrency() {
        return secondCurrency;
    }
}
