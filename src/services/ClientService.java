package services;

import database.CRUD;
import database.Database;
import database.IDatabaseOperations;
import users.Client;
import users.Person;

import java.sql.*;
import java.util.*;

public class ClientService implements CRUD<Client>, IDatabaseOperations<Client> {
    private static ClientService clientServiceInstance = null;
    private List<Client> clients;
    HashSet<String> usernames;
    HashSet<String> emails;
    private Client currentClient;
    private static Scanner scan = new Scanner(System.in);

    private ClientService() {}

    public static ClientService getInstance() {
        if (clientServiceInstance == null)
            clientServiceInstance = new ClientService();
        return clientServiceInstance;
    }

    public List<Client> getClients() { return clients; }

    public Client getCurrentClient() {
        return currentClient;
    }

    public void LogIn() {
        if (currentClient != null) {
            System.out.println("You are already logged in with an account");
            return;
        }
        System.out.print("Introduceti username-ul: ");
        String username = scan.nextLine();
        System.out.print("Introduceti parola: ");
        String password = scan.nextLine();
        for (Client cl: clients) {
            if (cl.getUsername().equals(username) && cl.checkPassword(password)) {
                currentClient = cl;
                try {
                    AccountService.getInstance().setClientAccounts(cl);
                    DepositService.getInstance().setClientDeposits(cl);
                    CreditService.getInstance().setClientCredits(cl);
                    TransactionService.getInstance().setTransactions(cl);
                    System.out.println("V-ati logat cu succes");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println("Nu exista nici un client cu aceasta combinatie de username si parola");
    }

    public void LogOut() {
        if (currentClient != null) {
            currentClient = null;
            AccountService.getInstance().setNull();
            DepositService.getInstance().setNull();
            CreditService.getInstance().setNull();
            TransactionService.getInstance().setNull();
            System.out.println("V-ati deconectat cu succes");
        } else
            System.out.println("You are not signed in to log out");
    }

    public void Register() {
        if (currentClient != null) {
            System.out.println("You have to log out first to register");
            return;
        }

        String username, email, tip;
        while (true) {
            System.out.print("Introduceti un username: ");
            username = scan.nextLine();
            if (!usernames.contains(username)) {
                break;
            }
            System.out.println("Username-ul introdus este indisponibil");
        }
        while (true) {
            System.out.print("Introduceti email-ul: ");
            email = scan.nextLine();
            if (!emails.contains(email)) {
                break;
            }
            System.out.println("Email-ul introdus este indisponibil");
        }
        System.out.print("Introduceti o parola: ");
        String password = scan.nextLine();
        System.out.print("Introducet numele dvs: ");
        String name = scan.nextLine();
        while (true) {
            System.out.print("Alegeti tipul persoanei care va corespune (Fizica sau Juridica): ");
            tip = scan.nextLine();
            if (tip.equals("Fizica") || tip.equals("Juridica")) {
                break;
            }
            System.out.println("Introduceti un tip valid");
        }
        int id = create(new Client(-1, username, password, name, email, tip));
        Client newClient = read(id);
        clients.add(newClient);
        usernames.add(username);
        emails.add(email);
        System.out.println("V-ati inregistrat cu success. Acum puteti va logati cu username-ul si parola specificate la inregistrare");
    }

    public void ChangeAccount() {
        if (currentClient == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a putea schimba parola");
            return;
        }
        System.out.print("Introduceti parola pentru verificare: ");
        while (true) {
            String checkPass = scan.nextLine();
            if (!currentClient.checkPassword(checkPass)) {
                System.out.print("Parola gresita! Introduceti parola corecta: ");
            } else break;
        }

        System.out.print("Doriti sa schimbati username-ul? (Y/N): ");
        String username = "";
        while (username.equals("")) {
            String ans = scan.nextLine();
            if (ans.equals("N")) break;
            else if (ans.equals("Y")) {
                System.out.print("Introduceti un nou username: ");
                while (true) {
                    String newUsername = scan.nextLine();
                    if (!usernames.contains(newUsername)) {
                        username = newUsername;
                        break;
                    }
                    System.out.println("Username-ul introdus este indisponibil!");
                }
            } else System.out.print("Introduceti unul din caracterele Y sau N: ");
        }

        System.out.print("Doriti sa schimbati parola? (Y/N): ");
        String password = "";
        while (password.equals("")) {
            String ans = scan.nextLine();
            if (ans.equals("N")) break;
            else if (ans.equals("Y")) {
                System.out.print("Introduceti o noua parola: ");
                while (true) {
                    String newPassword = scan.nextLine();
                    if (!newPassword.equals("")) {
                        password = newPassword;
                        break;
                    }
                    System.out.println("Introduceti o parola valida!");
                }
            } else System.out.print("Introduceti unul din caracterele Y sau N: ");
        }

        if (!username.equals("") || !password.equals("")) {
            if (!username.equals("")) currentClient.setUsername(username);
            if (!password.equals("")) currentClient.setPassword(password);
            update(currentClient.getID(), currentClient);
            System.out.println("Contul dvs a fost actualizat cu succes!");
        }
    }

    public void DeleteAccount() {
        if (currentClient == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a putea sterge contul");
            return;
        }
        if (currentClient.getAccounts().size() > 0 || currentClient.getDeposits().size() > 0 || currentClient.getCredits().size() > 0) {
            System.out.println("Nu puteti sterge contul atat timp cat aveti conturi bancare, depozite sau credite deschise!");
            return;
        }

        System.out.println("Sunteti sigur/sigura ca doriti sa stergeti contul? (Y/N): ");
        while (true) {
            String ans = scan.nextLine();
            if (ans.equals("N")) break;
            else if (ans.equals("Y")) {
                delete(currentClient.getID());
                LogOut();
                System.out.println("Accountul a fost sters cu succes");
                return;
            } else System.out.print("Introduceti unul din caracterele Y sau N: ");
        }
    }

    public void printClientInfo() {
        if (currentClient != null) {
            System.out.println(currentClient.toString());
        } else {
            System.out.println("Trebuie sa va logati mai intai pentru a afisa informatii");
        }
    }

    public Client selectClientFromOthers() {
        if (currentClient == null) {
            System.out.println("Trebuie sa va logati pentru a efectua aceasta operatie");
            return null;
        }
        List<Person> otherUsers = new ArrayList<>(clients);
        otherUsers.remove(currentClient);
        for (int i = 0; i < otherUsers.size(); i++)
            System.out.println((i + 1) + ") " + otherUsers.get(i).getName());
        while (true) {
            int idx = scan.nextInt();
            if (1 <= idx && idx <= otherUsers.size())
                return (Client) otherUsers.get(idx - 1);
            System.out.println("Introduceti un numar valid");
        }
    }

    /*
        Database related operations
     */

    @Override
    public List<Client> getCollection() { return clients; }

    @Override
    public void load() {
        this.clients = readAll();
        usernames = new HashSet<>();
        emails = new HashSet<>();
        for (Client cl: clients) {
            usernames.add(cl.getUsername());
            emails.add(cl.getEmail());
        }
    }

    @Override
    public Client toObjectFromDB(String[] dbRow) {
        int id = Integer.parseInt(dbRow[0]);
        String username = dbRow[1];
        String password = dbRow[2];
        String name = dbRow[3];
        String email = dbRow[4];
        String clientType = dbRow[5];
        return new Client(id, username, password, name, email, clientType);
    }

    @Override
    public String[] toDBString(Client cl) {
        return new String[] {
                cl.getID().toString(),
                cl.getUsername(),
                cl.getPassword(),
                cl.getName(),
                cl.getEmail(),
                cl.getClientType()
        };
    }

    @Override
    public Client getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Client> maybeClient =
                clients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst();
        return maybeClient.orElse(null);
    }

    public void setData() {
        for (Client client: clients) {
            client.setAccounts(AccountService.getInstance().getAccountsByClientId(client.getID()));
            client.setDeposits(DepositService.getInstance().getDepositsByClientId(client.getID()));
            client.setCredits(CreditService.getInstance().getCreditsByClientId(client.getID()));
            client.setTransactions(new TreeSet<>(TransactionService.getInstance().getTransactionsByClientId(client.getID())));
        }
    }

    public void setTransactions() {
        for (Client client: clients)
            client.setTransactions(new TreeSet<>(TransactionService.getInstance().getTransactionsByClientId(client.getID())));
    }

    /* CRUD Operations */
    @Override
    public int create(Client obj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String insertQuery = "insert into client values (null, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, obj.getUsername());
            preparedStatement.setString(2, obj.getPassword());
            preparedStatement.setString(3, obj.getName());
            preparedStatement.setString(4, obj.getEmail());
            preparedStatement.setString(5, obj.getClientType());

            preparedStatement.executeUpdate();
            ResultSet genKeys = preparedStatement.getGeneratedKeys();
            int id = -1;
            if (genKeys.next()) id = genKeys.getInt(1);
            preparedStatement.close();

            return id;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @ClientService/create: " + sqle.getMessage());
            return -1;
        }
    }

    @Override
    public List<Client> readAll() {
        List<Client> databaseClients = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select id from client";
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                databaseClients.add(read(id));
            }
            return databaseClients;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @ClientService/readALl: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public Client read(int clientId) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String selectQuery = "select * from client where id = " + Integer.toString(clientId);
            ResultSet queryResult = connection.prepareStatement(selectQuery).executeQuery();
            Client client = null;
            while (queryResult.next()) {
                int id = queryResult.getInt("id");
                String username = queryResult.getString("username");
                String password = queryResult.getString("password");
                String name = queryResult.getString("name");
                String email = queryResult.getString("email");
                String clientType = queryResult.getString("clientType");
                client = new Client(id, username, password, name, email, clientType);
            }
            return client;
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @ClientService/read: " + sqle.getMessage());
            return null;
        }
    }

    @Override
    public void update(int id, Client newObj) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword())) {
            String updateQuery = "update client set username = ?, password = ?, email = ?, name = ?, clientType = ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newObj.getUsername());
            preparedStatement.setString(2, newObj.getPassword());
            preparedStatement.setString(3, newObj.getEmail());
            preparedStatement.setString(4, newObj.getName());
            preparedStatement.setString(5, newObj.getClientType());
            preparedStatement.setInt(6, newObj.getID());

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @ClientService/update: " + sqle.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(Database.getInstance().getUrl(), Database.getInstance().getUser(), Database.getInstance().getPassword());
             Statement statement = connection.createStatement()) {
            String deleteQuery = "delete from client where id = " + id;
            statement.executeUpdate(deleteQuery);
        } catch (SQLException sqle) {
            System.out.println("SQL Exception @ClientService/delete: " + sqle.getMessage());
        }
    }
}
