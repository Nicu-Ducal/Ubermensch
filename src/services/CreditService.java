package services;

import database.CRUD;
import database.Database;
import database.IDatabaseOperations;
import features.Credit;
import features.Currency;
import features.interfaces.Numeric;
import users.Client;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CreditService implements CRUD<Credit>, IDatabaseOperations<Credit> {
    private static CreditService creditServiceInstance = null;
    private List<Credit> credits;
    private List<Credit> clientCredits;
    private Credit selectedCredit;
    HashMap<String, Double> typeDobanda;
    private final Double MAX_CREDIT_AMOUNT = 50000000.0;
    private final int CREDITS_LIMIT = 5;
    private static Scanner scan = new Scanner(System.in);

    private CreditService() { setNull(); }

    public static CreditService getInstance() {
        if (creditServiceInstance == null)
            creditServiceInstance = new CreditService();
        return creditServiceInstance;
    }

    public void setNull() {
        clientCredits = null;
        selectedCredit = null;
    }

    public void setClientCredits(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientCredits = client.getCredits();
        for (Credit cr: clientCredits)
            cr.updateAccountBalance();
    }

    public void selectCredit(String index) {
        if (clientCredits == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a selecta un credit");
            return;
        } else if (clientCredits.size() == 0) {
            System.out.println("Nu aveti nici un credit deschis");
            return;
        }
        try {
            if (Numeric.isInteger(index)) {
                selectCreditWithIndex(Integer.parseInt(index));
                System.out.println("Creditul a fost selectat cu succes");
            } else {
                System.out.println("Introduceti o valoare intreaga");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectCreditWithIndex(int index) throws Exception {
        int size = clientCredits.size();
        if (1 <= index && index <= size) selectedCredit = clientCredits.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un credit cu asa numar");
    }

    public void showCreditsInfo() {
        if (clientCredits == null) {
            System.out.println("Nu sunteti logat pentru a afisa informatiile despre creditele dvs.");
        } else if (clientCredits.size() == 0) {
            System.out.println("Nu aveti nici un credit deschis");
        } else {
            System.out.println("Creditele bancare: ");
            for (int i = 0; i < clientCredits.size(); i++)
                System.out.println((i + 1) + ") " + clientCredits.get(i));
        }
    }

    public void informatiiCredit() {
        if (selectedCredit != null) selectedCredit.extras();
        else System.out.println("Selectati un credit pentru a afisa extrasul");
    }

    public void addMoneyToCredit(Double amount) {
        if (clientCredits == null) {
            System.out.println("Trebuie sa va logati pentru a putea efectua aceasta tranzactie");
            return;
        }
        if (selectedCredit != null) selectedCredit.payCredit(amount);
        else System.out.println("Selectati un credit pentru a putea adauga bani pe el");
    }

    public void openCredit(ClientService cls, CurrencyService crs) throws Exception {
        if (cls.getCurrentClient() == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a deschide un credit");
            return;
        }
        String line;
        int depositsNumber = clientCredits.size();
        if (depositsNumber > CREDITS_LIMIT) {
            throw new Exception("Aveti deja limita admisibila de credite. Achitati unele din cele curente pentru a putea deschide unul nou");
        }
        Currency currency = crs.selectCurrency();
        System.out.print("Introduceti suma de bani pe care doriti sa o imprumutati: ");
        Double balance = Numeric.getBalance(scan, MAX_CREDIT_AMOUNT);
        String type = printAndGetType();
        Double dobanda = typeDobanda.get(type);
        int id = create(new Credit(-1, cls.getCurrentClient(), currency, dobanda, type, balance, 0.0, LocalDateTime.now()));
        Credit newCredit = read(id);
        clientCredits.add(newCredit);
        credits.add(newCredit);
        System.out.println("Creditul a fost deschis cu succes!");
    }

    public void closeCredit() {
        if (clientCredits == null) {
            System.out.println("Trebuie sa fiti logat si sa aveti un Creditul selectat pentru a-l sterge");
            return;
        }
        if (selectedCredit == null) {
            System.out.println("Selectati un credit pentru a-l sterge");
            return;
        }
        if (selectedCredit.getSumaRestituita() < selectedCredit.getSumaDeRestituit()) {
            System.out.println("Nu puteti inchide un credit nerambursat");
            return;
        }

        clientCredits.remove(selectedCredit);
        credits.remove(selectedCredit);
        delete(selectedCredit.getID());
        selectedCredit = null;
        System.out.println("Creditul a fost sters cu succes");
    }

    public void payCredit(String amount, String currency) {
        if (clientCredits == null) {
            System.out.println("Trebuie sa fiti logat si sa aveti un Creditul selectat pentru a-l sterge");
            return;
        }
        if (selectedCredit == null) {
            System.out.println("Selectati un credit pentru a-l sterge");
            return;
        }
        if (selectedCredit.getSumaRestituita() >= selectedCredit.getSumaDeRestituit()) {
            System.out.println("Creditul este achitat");
            return;
        }

        Currency cr = CurrencyService.getInstance().getCurrency(currency);
        if (cr == null) {
            System.out.println("Valuta introdusa de dvs. nu exista");
            return;
        }
        if (Numeric.isNumeric(amount) && Double.parseDouble(amount) > 0.0) {
            Double moneyAmount = Numeric.RoundTwoDecimals(Double.parseDouble(amount));
            try {
                moneyAmount = CurrencyService.getInstance().convert(moneyAmount, cr, selectedCredit.getAccountCurrency());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }

            selectedCredit.payCredit(moneyAmount);
            update(selectedCredit.getID(), selectedCredit);
            System.out.println("Creditul dvs. a fost rambursat cu " + moneyAmount + " " + selectedCredit.getAccountCurrency().getName());
        } else
            System.out.println("Introduceti o valoare numerica pozitiva pentru suplinirea creditului");
    }

    private String printAndGetType() {
        System.out.print("Alegeti tipul de credit pe care vreti sa-l deschideti:\n" +
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
    public List<Credit> getCollection() { return credits; }

    @Override
    public void load() {
        this.credits = readAll();
        this.typeDobanda = readCreditDobanda();
    }

    @Override
    public Credit toObjectFromDB(String[] dbRow) {
        int id = Integer.parseInt(dbRow[0]);
        Integer clientID = Integer.parseInt(dbRow[1]);
        Integer currencyID = Integer.parseInt(dbRow[2]);
        Client client = ClientService.getInstance().getElementById(clientID);
        Currency currency = CurrencyService.getInstance().getElementById(currencyID);
        Double dobanda = Double.parseDouble(dbRow[3]);
        String type = dbRow[4];
        Double sumaImprumutata = Double.parseDouble(dbRow[5]);
        Double sumaRestituita = Double.parseDouble(dbRow[6]);
        LocalDateTime creationDate = LocalDateTime.parse(dbRow[7]);
        return new Credit(id, client, currency, dobanda, type, sumaImprumutata, sumaRestituita, creationDate);
    }

    @Override
    public String[] toDBString(Credit obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getClient().getID().toString(),
                obj.getAccountCurrency().getID().toString(),
                obj.getDobanda().toString(),
                obj.getType(),
                obj.getSumaImprumutata().toString(),
                obj.getSumaRestituita().toString(),
                obj.getCreationDate().toString()
        };
    }

    @Override
    public Credit getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Credit> maybeCredit =
                credits.stream()
                        .filter(credit -> credit.getID().equals(id))
                        .findFirst();
        return maybeCredit.orElse(null);
    }

    public List<Credit> getCreditsByClientId(Integer clientID) {
        List<Credit> clCredits =
                credits.stream()
                        .filter(credit -> credit.getClient().getID().equals(clientID))
                        .collect(Collectors.toList());
        return clCredits;
    }

    /* CRUD Operations */
    @Override
    public int create(Credit obj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String insertQuery = "insert into credit values (null, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, obj.getClient().getID());
            preparedStatement.setInt(2, obj.getAccountCurrency().getID());
            preparedStatement.setDouble(3, obj.getDobanda());
            preparedStatement.setString(4, obj.getType());
            preparedStatement.setDouble(5, obj.getSumaImprumutata());
            preparedStatement.setDouble(6, obj.getSumaRestituita());
            preparedStatement.setString(7, obj.getCreationDate().toString());

            preparedStatement.executeUpdate();
            ResultSet genKeys = preparedStatement.getGeneratedKeys();
            int id = -1;
            if (genKeys.next()) id = genKeys.getInt(1);
            preparedStatement.close();

            return id;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/create: " + sqle.getMessage());
            return -1;
        }
    }

    @Override
    public List<Credit> readAll() {
        List<Credit> databaseCredits = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select id from credit";
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                databaseCredits.add(read(id));
            }
            return databaseCredits;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/readALl: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public Credit read(int creditId) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select * from credit where id =" + Integer.toString(creditId);
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            Credit credit = null;
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                int clientID = queryResult.getInt("clientID");
                int currencyID = queryResult.getInt("currencyID");
                Double dobanda = queryResult.getDouble("dobanda");
                String type = queryResult.getString("type");
                Double sumaImprumutata = queryResult.getDouble("sumaImprumutata");
                Double sumaRestituita = queryResult.getDouble("sumaRestituita");
                LocalDateTime creationDate = LocalDateTime.parse(queryResult.getString("creationDate"));

                Client client = ClientService.getInstance().getElementById(clientID);
                Currency currency = CurrencyService.getInstance().getElementById(currencyID);
                credit = new Credit(id, client, currency, dobanda, type, sumaImprumutata, sumaRestituita, creationDate);
            }
            return credit;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/read: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public void update(int id, Credit newObj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String updateQuery = "update credit set sumaRestituita = " + newObj.getSumaRestituita() + " where id = " + id;
            statement.executeUpdate(updateQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/update: " + sqle.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String deleteQuery = "delete from credit where id = " + id;
            statement.executeUpdate(deleteQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/delete: " + sqle.getMessage());
        }
    }

    public HashMap<String, Double> readCreditDobanda() {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            HashMap<String, Double> databaseCreditDobanda = new HashMap<>();
            String selectQuery = "select * from creditsdobanda";
            ResultSet result = statement.executeQuery(selectQuery);
            while (result.next()) {
                String name = result.getString("name");
                Double dobanda = result.getDouble("dobanda");
                databaseCreditDobanda.put(name, dobanda);
            }
            return databaseCreditDobanda;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @CreditService/readCreditDobanda: " + sqle.getMessage());
            return null;
        }
    }
}
