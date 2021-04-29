package services;

import database.IDatabaseOperations;
import users.Client;
import users.Person;

import java.util.*;

public class ClientService implements IDatabaseOperations<Client> {
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

    public void LogIn(AccountService as, DepositService ds, CreditService cs, TransactionService ts) {
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
                    as.setClientAccounts(cl);
                    ds.setClientDeposits(cl);
                    cs.setClientCredits(cl);
                    ts.setTransactions(cl);
                    System.out.println("V-ati logat cu succes");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println("Nu exista nici un client cu aceasta combinatie de username si parola");
    }

    public void LogOut(AccountService as, DepositService ds, CreditService cs, TransactionService ts) {
        if (currentClient != null) {
            currentClient = null;
            as.setNull();
            ds.setNull();
            cs.setNull();
            ts.setNull();
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
        Integer id = clients.size() == 0 ? 1 : clients.get(clients.size() - 1).getID() + 1;
        clients.add(new Client(id, username, password, name, email, tip));
        usernames.add(username);
        emails.add(email);
        System.out.println("V-ati inregistrat cu success. Acum puteti va logati cu username-ul si parola specificate la inregistrare");
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
    public void load(List<Client> clients) {
        this.clients = clients;
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
}
