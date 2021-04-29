package database;

import features.Currency;
import features.ExchangeCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    private static Database dbInstance = null;

    private Database() {}

    public static Database getInstance() {
        if (dbInstance == null)
            dbInstance = new Database();
        return dbInstance;
    }

    public void loadDatabase() {

    }

    public List<Currency> createCurrencies() {
        List<Currency> db = new ArrayList<>();
        db.add(new Currency(0,"Lei", "RON"));
        db.add(new Currency(1,"Euro", "EUR"));
        db.add(new Currency(2,"Dolari", "USD"));
        db.add(new Currency(3,"Lire Sterline", "GBP"));
        return db;
    }

    public HashMap<ExchangeCurrency, Double> createExchanges() {
        HashMap<ExchangeCurrency, Double> db = new HashMap<>();
        List<Currency> currencies = createCurrencies();
        Currency euro = currencies.get(1);
        db.put(new ExchangeCurrency(euro, currencies.get(0)), 4.85);
        db.put(new ExchangeCurrency(euro, currencies.get(2)), 1.19);
        db.put(new ExchangeCurrency(euro, currencies.get(3)), 0.86);
        return db;
    }

    public HashMap<String, Double> createDepositDobanda() {
        HashMap<String, Double> db = new HashMap<>();
        db.put("Lunar", 2.0);
        db.put("Anual", 5.0);
        db.put("Cincinal", 10.0);
        return db;
    }

    public HashMap<String, Double> createCreditDobanda() {
        HashMap<String, Double> db = new HashMap<>();
        db.put("Lunar", 2.0);
        db.put("Anual", 15.0);
        db.put("Cincinal", 40.0);
        return db;
    }
}
