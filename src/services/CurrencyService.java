package services;

import features.Currency;
import features.interfaces.Numeric;

import java.util.List;
import java.util.Scanner;

public class CurrencyService {
    private final List<Currency> currencies;
    // TODO: Add ExchangeCurrencies Set or Map

    public CurrencyService(List<Currency> currencies) {
        this.currencies = currencies;
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
}
