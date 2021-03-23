package app;

import features.Account;
import features.Currency;
import users.Client;
import users.Person;

import java.util.ArrayList;
import java.util.List;

public class BankApp {
    private static BankApp bank = null;
    private List<Person> clientList;
    private List<Person> employeeList;
    Boolean running;

    private BankApp() {
        clientList = new ArrayList<>();
        employeeList = new ArrayList<>();
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
