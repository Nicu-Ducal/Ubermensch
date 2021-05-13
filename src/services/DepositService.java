package services;

import database.CRUD;
import database.Database;
import database.IDatabaseOperations;
import features.Currency;
import features.Deposit;
import features.interfaces.Numeric;
import users.Client;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DepositService implements CRUD<Deposit>, IDatabaseOperations<Deposit> {
    private static DepositService depositServiceInstance = null;
    private List<Deposit> deposits;
    private List<Deposit> clientDeposits;
    private Deposit selectedDeposit;
    HashMap<String, Double> typeDobanda;
    private static Scanner scan = new Scanner(System.in);
    private final int DEPOSITS_LIMIT = 10;
    private final Double MAX_DEPOSIT_BALANCE = 100000000.0;

    private DepositService() { setNull(); }

    public static DepositService getInstance() {
        if (depositServiceInstance == null)
            depositServiceInstance = new DepositService();
        return depositServiceInstance;
    }

    public void setClientDeposits(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientDeposits = client.getDeposits();
        for (Deposit d: clientDeposits)
            d.updateAccountBalance();
    }

    public void setNull() {
        clientDeposits = null;
        selectedDeposit = null;
    }

    public void selectDeposit(String index) {
        if (clientDeposits == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a selecta un depozit");
            return;
        } else if (clientDeposits.size() == 0) {
            System.out.println("Nu aveti nici un deposit deschis");
            return;
        }
        try {
            if (Numeric.isInteger(index)) {
                selectDepositWithIndex(Integer.parseInt(index));
                System.out.println("Depozitul a fost selectat cu succes");
            } else {
                System.out.println("Introduceti o valoare intreaga");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectDepositWithIndex(int index) throws Exception {
        int size = clientDeposits.size();
        if (1 <= index && index <= size) selectedDeposit = clientDeposits.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un deposit cu asa numar");
    }

    public void showDepositsInfo() {
        if (clientDeposits == null) {
            System.out.println("Nu sunteti logat pentru a afisa informatiile despre depozite bancare");
        } else if (clientDeposits.size() == 0) {
            System.out.println("Nu aveti nici un depozit deschis");
        } else {
            System.out.println("Depozitele bancare: ");
            for (int i = 0; i < clientDeposits.size(); i++)
                System.out.println((i + 1) + ") " + clientDeposits.get(i));
        }
    }

    public void informatiiDepozit() {
        if (selectedDeposit != null) {
            selectedDeposit.extras();
            selectedDeposit.checkDepositStatus();
        }
        else System.out.println("Selectati un depozit pentru a afisa informatii despre acesta");
    }

    public void openDeposit(ClientService cls, CurrencyService crs) throws Exception {
        if (cls.getCurrentClient() == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a deschide un depozit");
            return;
        }
        String line;
        int depositsNumber = clientDeposits.size();
        if (depositsNumber > DEPOSITS_LIMIT) {
            throw new Exception("Aveti deja limita admisibila de depozite deschise. Inchideti unele din cele curente pentru a deschide unul nou");
        }
        Currency currency = crs.selectCurrency();
        System.out.print("Introduceti suma de bani pentru deposit: ");
        Double balance = Numeric.getBalance(scan, MAX_DEPOSIT_BALANCE);
        String type = printAndGetType();
        Double dobanda = typeDobanda.get(type);
        int id = create(new Deposit(-1, cls.getCurrentClient(), currency, dobanda, type, balance, LocalDateTime.now(), LocalDateTime.now().plusYears(1)));
        Deposit newDeposit = read(id);
        clientDeposits.add(newDeposit);
        deposits.add(newDeposit);
        System.out.println("Depositul a fost deschis cu succes!");
    }

    public void closeDeposit() {
        if (clientDeposits == null) {
            System.out.println("Trebuie sa fiti logat si sa aveti un depozit selectat pentru a-l sterge");
            return;
        }
        if (selectedDeposit == null) {
            System.out.println("Selectati un depozit pentru a-l sterge");
            return;
        }
        clientDeposits.remove(selectedDeposit);
        deposits.remove(selectedDeposit);
        delete(selectedDeposit.getID());
        selectedDeposit = null;
        System.out.println("Depositul a fost sters cu succes");
    }

    private String printAndGetType() {
        System.out.print("Alegeti tipul de deposit pe care vreti sa-l deschideti:\n" +
                "1) Lunar - Dobanda: " + typeDobanda.get("Lunar") + "\n" +
                "2) Anual - Dobanda: " + typeDobanda.get("Anual") + "\n" +
                "3) Cincinal - Dobanda: " + typeDobanda.get("Cincinal") + "\n");
        while (true) {
            String type = scan.nextLine();
            if (type.equals("Lunar") || type.equals("Anual") || type.equals("Cincinal"))
                return type;
            System.out.println("Introduceti un tip de deposit din cele de mai sus!");
        }
    }

    /*
        Database related operations
     */

    @Override
    public List<Deposit> getCollection() { return deposits; }

    @Override
    public void load() {
        this.deposits = readAll();
        this.typeDobanda = readDepositDobanda();
    }

    @Override
    public Deposit toObjectFromDB(String[] dbRow) {
        int id = Integer.parseInt(dbRow[0]);
        Integer clientID = Integer.parseInt(dbRow[1]);
        Integer currencyID = Integer.parseInt(dbRow[2]);
        Client client = ClientService.getInstance().getElementById(clientID);
        Currency currency = CurrencyService.getInstance().getElementById(currencyID);
        Double dobanda = Double.parseDouble(dbRow[3]);
        String type = dbRow[4];
        Double sumaDepusa = Double.parseDouble(dbRow[5]);
        LocalDateTime creationDate = LocalDateTime.parse(dbRow[6]);
        LocalDateTime expirationDate = LocalDateTime.parse(dbRow[7]);
        return new Deposit(id, client, currency, dobanda, type, sumaDepusa, creationDate, expirationDate);
    }

    @Override
    public String[] toDBString(Deposit obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getClient().getID().toString(),
                obj.getAccountCurrency().getID().toString(),
                obj.getDobanda().toString(),
                obj.getType(),
                obj.getSumaDepusa().toString(),
                obj.getCreationDate().toString(),
                obj.getExpirationDate().toString()
        };
    }

    @Override
    public Deposit getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Deposit> maybeDeposit =
                deposits.stream()
                        .filter(deposit -> deposit.getID().equals(id))
                        .findFirst();
        return maybeDeposit.orElse(null);
    }

    public List<Deposit> getDepositsByClientId(Integer clientID) {
        List<Deposit> clDeposits =
                deposits.stream()
                        .filter(deposit -> deposit.getClient().getID().equals(clientID))
                        .collect(Collectors.toList());
        return clDeposits;
    }

    /* CRUD Operations */
    @Override
    public int create(Deposit obj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String insertQuery = "insert into deposit values (null, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, obj.getClient().getID());
            preparedStatement.setInt(2, obj.getAccountCurrency().getID());
            preparedStatement.setDouble(3, obj.getDobanda());
            preparedStatement.setString(4, obj.getType());
            preparedStatement.setDouble(5, obj.getSumaDepusa());
            preparedStatement.setString(6, obj.getCreationDate().toString());
            preparedStatement.setString(7, obj.getExpirationDate().toString());

            preparedStatement.executeUpdate();
            ResultSet genKeys = preparedStatement.getGeneratedKeys();
            int id = -1;
            if (genKeys.next()) id = genKeys.getInt(1);
            preparedStatement.close();

            return id;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/create: " + sqle.getMessage());
            return -1;
        }
    }

    @Override
    public List<Deposit> readAll() {
        List<Deposit> databaseDeposits = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select id from deposit";
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                databaseDeposits.add(read(id));
            }
            return databaseDeposits;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/readALl: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public Deposit read(int depositId) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select * from deposit where id =" + Integer.toString(depositId);
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            Deposit deposit = null;
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                int clientID = queryResult.getInt("clientID");
                int currencyID = queryResult.getInt("currencyID");
                Double dobanda = queryResult.getDouble("dobanda");
                String type = queryResult.getString("type");
                Double sumaDepusa = queryResult.getDouble("sumaDepusa");
                LocalDateTime creationDate = LocalDateTime.parse(queryResult.getString("creationDate"));
                LocalDateTime expirationDate = LocalDateTime.parse(queryResult.getString("expirationDate"));

                Client client = ClientService.getInstance().getElementById(clientID);
                Currency currency = CurrencyService.getInstance().getElementById(currencyID);
                deposit = new Deposit(id, client, currency, dobanda, type, sumaDepusa, creationDate, expirationDate);
            }
            return deposit;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/read: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public void update(int id, Deposit newObj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String updateQuery = "update deposit set expirationDate = " + newObj.getExpirationDate().toString() + " where id = " + id;
            statement.executeUpdate(updateQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/update: " + sqle.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String deleteQuery = "delete from deposit where id = " + id;
            statement.executeUpdate(deleteQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/delete: " + sqle.getMessage());
        }
    }

    public HashMap<String, Double> readDepositDobanda() {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            HashMap<String, Double> databaseDepositDobanda = new HashMap<>();
            String selectQuery = "select * from depositsdobanda";
            ResultSet result = statement.executeQuery(selectQuery);
            while (result.next()) {
                String name = result.getString("name");
                Double dobanda = result.getDouble("dobanda");
                databaseDepositDobanda.put(name, dobanda);
            }
            return databaseDepositDobanda;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @DepositService/readDepositDobanda: " + sqle.getMessage());
            return null;
        }
    }
}
