package users;

import features.Account;
import features.Credit;
import features.Deposit;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Client extends Person {
    private String clientType;
    private List<Account> accounts;
    private SortedSet<Deposit> deposits;
    private SortedSet<Credit> credits;


    public Client(int id, String username, String password, String name, String email, String clientType) {
        super(id, username, password, name, email);
        this.clientType = clientType;
        this.accounts = new ArrayList<>();
        this.deposits = new TreeSet<>();
        this.credits = new TreeSet<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public SortedSet<Deposit> getDeposits() {
        return deposits;
    }

    public SortedSet<Credit> getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return ("Informatii despre clientul " + name + ":\n"
                + "Username: " + username + "\nEmail: " + email
                + "\nTip client: " + clientType + "\n");
    }
}
