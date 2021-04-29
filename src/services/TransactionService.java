package services;

import database.IDatabaseOperations;
import features.Account;
import features.Currency;
import features.Transaction;
import features.interfaces.Numeric;
import users.Client;

import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class TransactionService implements IDatabaseOperations<Transaction> {
    public static TransactionService transactionServiceInstance = null;
    private static Scanner scan = new Scanner(System.in);
    private final Double MAX_TRANSACTION_VALUE = 10000.0;
    Set<Transaction> clientTransactions;
    private Integer lastId = 0;

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
        clientTransactions.add(new Transaction(lastId++, from, other, amount, currency));
        other.getClient().getTransactions().add(new Transaction(lastId++, other, from, amount, currency));
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
    public Transaction toObjectFromDB(String[] dbRow, Object... services) {
        Integer id = Integer.parseInt(dbRow[0]);
        Integer accountFromID = Integer.parseInt(dbRow[1]);
        Integer accountToID = Integer.parseInt(dbRow[2]);
        Integer currencyID = Integer.parseInt(dbRow[3]);
        return null;
    }

    @Override
    public String[] toDBString(Transaction obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getFromAccount().getID().toString(),
                obj.getToAccount().getID().toString(),
                obj.getCurrency().getID().toString()
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
}
