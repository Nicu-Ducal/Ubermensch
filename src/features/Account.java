package features;

import users.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Account {
    private Integer id;
    private LocalDateTime creationDate;
    private Double balance;
    private Client client;
    private Currency accountCurrency;
    private CreditCard linkedCard;

    public Account(Integer id, Client client, Currency accountCurrency, Double balance) {
        this.id = id;
        this.client = client;
        this.accountCurrency = accountCurrency;
        this.balance = balance;
        this.creationDate = LocalDateTime.now();
        linkedCard = null;
    }

    public Account(Integer id, LocalDateTime creationDate, Double balance, Client client, Currency accountCurrency, CreditCard linkedCard) {
        this.id = id;
        this.creationDate = creationDate;
        this.balance = balance;
        this.client = client;
        this.accountCurrency = accountCurrency;
        this.linkedCard = linkedCard;
    }

    public Integer getID() { return id; }

    public Client getClient() {
        return client;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    public Double getBalance() {
        return balance;
    }

    public void withdrawMoney(Double amount) throws Exception {
        if (amount > balance) {
            throw new Exception("Not enough money on your account");
        }
        balance -= amount;
    }

    public void addMoney(Double amount) {
        balance += amount;
    }

    @Override
    public String toString() {
        return "Cont bancar, client: " + client.getName() + ", valuta: " + accountCurrency.getFullName();
    }

    public void extrasDeCont() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm");
        System.out.println("Extras pentru contul clientului " + client.getName() + " de la data de " + ft.format(now));
        System.out.println("Sold curent: " + balance + " " + accountCurrency.getName() + " (" + accountCurrency.getInternational() + ")");
        // Tranzactii
    }

    public Boolean emiteCard() {
        if (linkedCard != null) {
            return false;
        }
        linkedCard = new CreditCard(this);
        return true;
    }

    public Boolean schimbaCard() {
        if (linkedCard == null) {
            return false;
        }
        linkedCard = new CreditCard(this);
        return true;
    }
}
