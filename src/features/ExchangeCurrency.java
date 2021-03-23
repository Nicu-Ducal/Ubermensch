package features;

public class ExchangeCurrency {
    private Currency firstCurrency;
    private Currency secondCurrency;
    private Double exchangeRate;        // Este de tipul 1 unitate de first = 1 * exchangeRate unitati second

    public ExchangeCurrency(Currency firstCurrency, Currency secondCurrency, Double exchangeRate) {
        this.firstCurrency = firstCurrency;
        this.secondCurrency = secondCurrency;
        this.exchangeRate = exchangeRate;
    }

    public Currency getFirstCurrency() {
        return firstCurrency;
    }

    public Currency getSecondCurrency() {
        return secondCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
