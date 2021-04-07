package features;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Transaction implements Comparable<Transaction> {
    private Account fromAccount;
    private Account toAccount;
    private LocalDateTime transactionTime;
    private Double amount;
    private Currency currency;

    public Transaction(Account fromAccount, Account toAccount, Double amount, Currency currency) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionTime = LocalDateTime.now();
        this.amount = amount;
        this.currency = currency;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public Double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return fromAccount.equals(that.fromAccount) && toAccount.equals(that.toAccount) && transactionTime.equals(that.transactionTime) && amount.equals(that.amount) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAccount, toAccount, transactionTime, amount, currency);
    }

    @Override
    public int compareTo(Transaction o) {
        if (this.transactionTime.equals(o.transactionTime)) return 1;
        else if (this.transactionTime.isBefore(o.transactionTime)) return -1;
        return 1;
    }

    @Override
    public String toString() {
        DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm");
        return "Transfer bancar in valoare de " + amount + " " + currency.getFullName() + " la " +
                ft.format(transactionTime) + " de la " + fromAccount.getClient().getName() + " catre " +
                toAccount.getClient().getName();
    }
}
