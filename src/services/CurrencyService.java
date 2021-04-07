package services;

import features.Currency;
import features.ExchangeCurrency;
import features.interfaces.Numeric;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class CurrencyService {
    private final List<Currency> currencies;
    HashMap<ExchangeCurrency, Double> exchangeRates;

    public CurrencyService(List<Currency> currencies, HashMap<ExchangeCurrency, Double> rates) {
        this.currencies = currencies;
        this.exchangeRates = rates;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public Currency selectCurrency() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Selectati una din valutele disponibile: ");
        int size = currencies.size();
        for (int i = 0; i < size; i++)
            System.out.println((i + 1) + ". " + currencies.get(i).getFullName());
        while (true) {
            String input = scan.nextLine();
            if (Numeric.isInteger(input)) {
                int id = Integer.parseInt(input);
                if (1 <= id && id <= size)
                    return currencies.get(id - 1);
                else
                    System.out.println("Numarul introdus nu reprezinta o valuta");
            } else {
                System.out.println("Introduceti un numar");
            }
        }
    }

    public Currency getCurrency(String name) {
        for (Currency cr: currencies)
            if (cr.getName().equals(name) || cr.getInternational().equals(name))
                return cr;
        return null;
    }

    public Double convert(Double amount, Currency fromCurr, Currency toCurr) throws Exception {
        if (fromCurr.equals(toCurr))
            return amount;
        ExchangeCurrency excr1 = new ExchangeCurrency(fromCurr, toCurr);
        ExchangeCurrency excr2 = new ExchangeCurrency(toCurr, fromCurr);
        if (exchangeRates.containsKey(excr1))
            return amount * exchangeRates.get(excr1);
        if (exchangeRates.containsKey(excr2))
            return amount * (1.0 / exchangeRates.get(excr2));
        throw new Exception("Nu exista posibilitatea de schimb valutar de la " + fromCurr.getFullName() + " la " + toCurr.getFullName());
    }
}
