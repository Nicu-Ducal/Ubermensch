package app;

import features.Account;
import features.Currency;
import services.*;
import users.Client;

import java.util.ArrayList;

public class BankApp {
    private static BankApp bank = null;
    ClientService clientService;
    AccountService accountService;
    CurrencyService currencyService;
    DepositService depositService;
    CreditService creditService;
    Boolean running;

    private BankApp() {
        clientService = new ClientService(new ArrayList<Client>());
        accountService = new AccountService();
        currencyService = new CurrencyService(new ArrayList<Currency>());
        Boolean running = true;
    }

    public static BankApp getInstance() {
        if (bank == null)
            bank = new BankApp();
        return bank;
    }

    public void run() {
        Client client1 = new Client(1, "username", "password", "Nicu Ducal", "n.d@gm.com", "Fizic");
        Account acc1 = new Account(client1, new Currency("Leu", "RON"), 200.0);

        acc1.addMoney(500.5);
        acc1.extrasDeCont();
    }
}
