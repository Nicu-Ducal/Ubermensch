package services;

import database.IDatabaseOperations;
import features.Account;
import features.Currency;
import features.Transaction;
import features.interfaces.Numeric;
import users.Client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionService implements IDatabaseOperations<Transaction> {
    private static TransactionService transactionServiceInstance = null;
    private List<Transaction> transactions;
    private static Scanner scan = new Scanner(System.in);
    private final Double MAX_TRANSACTION_VALUE = 10000.0;
    Set<Transaction> clientTransactions;

    private TransactionService() { setNull(); }

    public static TransactionService getInstance() {
        if (transactionServiceInstance == null)
            transactionServiceInstance = new TransactionService();
        return transactionServiceInstance;
    }

    public void setTransactions(Client cl) {
        clientTransactions = cl.getTransactions();
    }

    public void setNull() {
        clientTransactions = null;
    }

    public void printTransactions() {
        if (clientTransactions.isEmpty()) {
            System.out.println("Nu aveti efectuata nici o tranzactie");
            return;
        }
        int idx = 1;
        for (Transaction tr : clientTransactions) {
            System.out.println(idx + ") " + tr);
            idx++;
        }
    }

    public void makeTransaction(ClientService cls, AccountService acs, CurrencyService crs) {
        if (cls.getCurrentClient() == null) {
            System.out.println("Trebuie sa va logati pentru a putea face un transfer bancar");
            return;
        }
        if (acs.getSelectedAccount() == null) {
            System.out.println("Trebuie sa selectati un cont bancar inainte de a face un transfer");
            return;
        }

        System.out.println("Selectati unul dintre clientii pentru a-i transfera bani: ");
        Client toClient = cls.selectClientFromOthers();
        Account toAccount = acs.selectAccountFromClient(toClient);
        System.out.print("Introduceti suma pe care doriti sa o transferati: ");
        Double amount = Numeric.getBalance(scan, MAX_TRANSACTION_VALUE);
        Currency currency = crs.selectCurrency();
        try {
            Double convertedAmount = crs.convert(amount, acs.getSelectedAccount().getAccountCurrency(), toAccount.getAccountCurrency());
            acs.withdrawBalance(amount.toString(), currency.getInternational(), crs);
            toAccount.addMoney(convertedAmount);
            System.out.println("Transferul a fost efectuat cu succes");
            addTransaction(acs.getSelectedAccount(), toAccount, amount, currency);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addTransaction(Account from, Account other, Double amount, Currency currency) {
        Integer id = transactions.size() == 0 ? 1 : transactions.get(transactions.size() - 1).getID() + 1;
        Transaction trans = new Transaction(id, from, other, amount, currency, null);
        clientTransactions.add(trans);
        other.getClient().getTransactions().add(trans);
        transactions.add(trans);
    }

    public void clearTransactions() {
        if (clientTransactions == null) {
            System.out.println("Trebuie sa va logati pentru a putea sterge tranzactiile");
        }
        clientTransactions.clear();
    }

    /*
        Database related operations
     */

    @Override
    public List<Transaction> getCollection() { return transactions; }

    @Override
    public void load(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public Transaction toObjectFromDB(String[] dbRow) {
        Integer id = Integer.parseInt(dbRow[0]);
        Integer accountFromID = Integer.parseInt(dbRow[1]);
        Integer accountToID = Integer.parseInt(dbRow[2]);
        Double amount = Double.parseDouble(dbRow[3]);
        Integer currencyID = Integer.parseInt(dbRow[4]);
        LocalDateTime creationDate = LocalDateTime.parse(dbRow[5]);
        Account fromAccount = AccountService.getInstance().getElementById(accountFromID);
        Account toAccount = AccountService.getInstance().getElementById(accountToID);
        Currency currency = CurrencyService.getInstance().getElementById(currencyID);
        return new Transaction(id, fromAccount, toAccount, amount, currency, creationDate);
    }

    @Override
    public String[] toDBString(Transaction obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getFromAccount().getID().toString(),
                obj.getToAccount().getID().toString(),
                obj.getAmount().toString(),
                obj.getCurrency().getID().toString(),
                obj.getTransactionTime().toString()
        };
    }

    @Override
    public Transaction getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Transaction> maybeTransaction =
                clientTransactions.stream()
                        .filter(transaction -> transaction.getID().equals(id))
                        .findFirst();
        return maybeTransaction.orElse(null);
    }

    public List<Transaction> getTransactionsByClientId(Integer clientID) {
        List<Transaction> clTransactions =
                transactions.stream()
                        .filter(transaction -> transaction.getFromAccount().getClient().getID().equals(clientID) || transaction.getToAccount().getClient().getID().equals(clientID))
                        .collect(Collectors.toList());
        return clTransactions;
    }
}
