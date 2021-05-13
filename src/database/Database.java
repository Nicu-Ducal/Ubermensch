package database;

import features.Currency;
import features.ExchangeCurrency;
import services.*;

import java.util.HashMap;
import java.util.List;

public class Database {
    private static Database dbInstance = null;
    private final String url = "jdbc:mysql://localhost:3306/ubermenschdb";
    private final String user = "root";
    private final String password = "351963";

    private Database() { }

    public static Database getInstance() {
        if (dbInstance == null)
            dbInstance = new Database();
        return dbInstance;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void loadDatabase() {
        // Load Clients
        ClientService.getInstance().load();

        // Load Currencies
        CurrencyService.getInstance().load();

        // Load Accounts
        AccountService.getInstance().load();

        // Load Deposits
        DepositService.getInstance().load();

        // Load Credits
        CreditService.getInstance().load();

        // Load Transactions
        TransactionService.getInstance().load();

        // Set client accounts, deposits, credits
        ClientService.getInstance().setData();
    }

    public HashMap<ExchangeCurrency, Double> createExchanges() {
        HashMap<ExchangeCurrency, Double> db = new HashMap<>();
        List<Currency> currencies = CurrencyService.getInstance().getCurrencies();
        Currency euro = currencies.get(1);
        db.put(new ExchangeCurrency(euro, currencies.get(0)), 4.85);
        db.put(new ExchangeCurrency(euro, currencies.get(2)), 1.19);
        db.put(new ExchangeCurrency(euro, currencies.get(3)), 0.86);
        return db;
    }
}
