package database;

import features.Currency;
import features.ExchangeCurrency;
import services.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Database {
    private static Database dbInstance = null;
    private CSVReader reader;
    private CSVWriter writer;

    private Database() {
        reader = CSVReader.getInstance();
        writer = CSVWriter.getInstance();
    }

    public static Database getInstance() {
        if (dbInstance == null)
            dbInstance = new Database();
        return dbInstance;
    }

    private String dbPath(String table) {
        return Paths.get(System.getProperty("user.dir"), "src", "database", "data", table + ".csv").toString();
    }

    private <T> void loadTable(String path, IDatabaseOperations<T> service) {
        service.load(reader.readData(dbPath(path), service));
    }

    private <T> void storeTable(String path, IDatabaseOperations<T> service) {
        writer.writeData(dbPath(path), service.getCollection(), service);
    }

    public void loadDatabase() {
        // Load the clients
        loadTable("Client", ClientService.getInstance());

        // Load the currencies
        loadTable("Currency", CurrencyService.getInstance());

        // Load the accounts
        loadTable("Account", AccountService.getInstance());

        // Load the deposits
        loadTable("Deposit", DepositService.getInstance());

        // Load the credits
        loadTable("Credit", CreditService.getInstance());

        // Load the transactions
        loadTable("Transaction", TransactionService.getInstance());

        // Set Accounts, Deposits, Credits and Transactions for Clients
        ClientService.getInstance().setData();
    }

    public void storeDatabase() {
        // Store clients
        storeTable("Client", ClientService.getInstance());

        // Store currencies
        storeTable("Currency", CurrencyService.getInstance());

        // Store accounts
        storeTable("Account", AccountService.getInstance());

        // Store deposits
        storeTable("Deposit", DepositService.getInstance());

        // Store credits
        storeTable("Credit", CreditService.getInstance());

        // Store transactions
        storeTable("Transaction", TransactionService.getInstance());
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
