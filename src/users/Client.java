package users;

import features.Account;
import features.Credit;
import features.Deposit;
import features.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Client extends Person {
    private Integer id;
    private String clientType;
    private List<Account> accounts;
    private List<Deposit> deposits;
    private List<Credit> credits;
    private Set<Transaction> transactions;

    public Client(int id, String username, String password, String name, String email, String clientType) {
        super(id, username, password, name, email);
        this.clientType = clientType;
        this.accounts = new ArrayList<>();
        this.deposits = new ArrayList<>();
        this.credits = new ArrayList<>();
        this.transactions = new TreeSet<>();
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

    public Set<Transaction> getTransactions() { return transactions; }

    public String getClientType() { return clientType; }

    @Override
    public String toString() {
        return ("Informatii despre clientul " + name + ":\n"
                + "Username: " + username + "\nEmail: " + email
                + "\nTip client: " + clientType
                + "\nNumarul de conturi bancare deschise: " + accounts.size()
                + "\nNumarul de depozite bancare deschise: " + deposits.size()
                + "\nNumarul de credite bancare deschise: " + credits.size()
                + "\nNumarul de transferuri efectuate: " + transactions.size());
    }
}
