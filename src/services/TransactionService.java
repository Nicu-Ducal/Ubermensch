package services;

import database.Database;
import database.IDatabaseOperations;
import features.Account;
import features.Currency;
import features.Transaction;
import features.interfaces.Numeric;
import users.Client;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
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
        try {
            Double convertedAmount = crs.convert(amount, acs.getSelectedAccount().getAccountCurrency(), toAccount.getAccountCurrency());
            if (acs.getSelectedAccount().getBalance() - convertedAmount < 0) {
                System.out.println("Nu aveti suficienti bani pe cont pentru a efectua transferul");
                return;
            }
            acs.getSelectedAccount().withdrawMoney(amount);
            toAccount.addMoney(convertedAmount);

            // Update the accounts' balances after transactions
            AccountService.getInstance().update(AccountService.getInstance().getSelectedAccount().getID(), AccountService.getInstance().getSelectedAccount());
            AccountService.getInstance().update(toAccount.getID(), toAccount);

            System.out.println("Transferul a fost efectuat cu succes");
            addTransaction(acs.getSelectedAccount(), toAccount, amount, acs.getSelectedAccount().getAccountCurrency());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addTransaction(Account from, Account other, Double amount, Currency currency) {
        int id = create(new Transaction(-1, from, other, amount, currency, null));
        Transaction trans = read(id);
        clientTransactions.add(trans);
        other.getClient().getTransactions().add(trans);
        transactions.add(trans);
    }

    /*
        Database related operations
     */

    @Override
    public List<Transaction> getCollection() { return transactions; }

    @Override
    public void load() {
        this.transactions = readAll();
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

    /* CRD Operations */
    public int create(Transaction obj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String insertQuery = "insert into transaction values (null, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, obj.getFromAccount().getID());
            preparedStatement.setInt(2, obj.getToAccount().getID());
            preparedStatement.setDouble(3, obj.getAmount());
            preparedStatement.setInt(4, obj.getCurrency().getID());
            preparedStatement.setString(5, obj.getTransactionTime().toString());

            preparedStatement.executeUpdate();
            ResultSet genKeys = preparedStatement.getGeneratedKeys();
            int id = -1;
            if (genKeys.next()) id = genKeys.getInt(1);
            preparedStatement.close();

            return id;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @TransactionService/create: " + sqle.getMessage());
            return -1;
        }
    }

    public List<Transaction> readAll() {
        List<Transaction> databaseTransactions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select id from transaction";
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                databaseTransactions.add(read(id));
            }
            return databaseTransactions;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @TransactionService/readALl: " + sqle.getMessage());
            return null;
        }
    }

    public Transaction read(int transactionId) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select * from transaction where id = " + Integer.toString(transactionId);
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            Transaction transaction = null;
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                int accountFromID = queryResult.getInt("accountFromID");
                int accountToID = queryResult.getInt("accountToID");
                Double amount = queryResult.getDouble("amount");
                int currencyID = queryResult.getInt("currencyID");
                LocalDateTime transactionTime = LocalDateTime.parse(queryResult.getString("transactionTime"));

                Account accountFrom = AccountService.getInstance().getElementById(accountFromID);
                Account accountTo = AccountService.getInstance().getElementById(accountToID);
                Currency currency = CurrencyService.getInstance().getElementById(currencyID);
                transaction = new Transaction(id, accountFrom, accountTo, amount, currency, transactionTime);
            }
            return transaction;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @TransactionService/read: " + sqle.getMessage());
            return null;
        }
    }

    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String deleteQuery = "delete from transaction where id = " + id;
            statement.executeUpdate(deleteQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @TransactionService/delete: " + sqle.getMessage());
        }
    }

    public void deleteTransactionsOfAccount(int id) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String deleteQuery = "delete from transaction where accountFromID = " + id + " or accountToID =" + id;
            statement.executeUpdate(deleteQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @TransactionService/delete: " + sqle.getMessage());
        }
        // Reset transactions
        load();
        ClientService.getInstance().setTransactions();
        this.clientTransactions = ClientService.getInstance().getCurrentClient().getTransactions();
    }
}
