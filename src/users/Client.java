package users;

import features.Account;
import features.Credit;
import features.Deposit;

import java.util.ArrayList;
import java.util.List;

public class Client extends Person {
    private String clientType;
    private List<Account> accounts;
    private List<Deposit> deposits;
    private List<Credit> credits;


    public Client(int id, String username, String password, String name, String email, String clientType) {
        super(id, username, password, name, email);
        this.clientType = clientType;
        this.accounts = new ArrayList<>();
        this.deposits = new ArrayList<>();
        this.credits = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    @Override
    public String toString() {
        return ("Informatii despre clientul " + name + ":\n"
                + "Username: " + username + "\nEmail: " + email
                + "\nTip client: " + clientType + "\n");
    }
}
