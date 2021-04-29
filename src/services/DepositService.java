package services;

import database.IDatabaseOperations;
import features.Currency;
import features.Deposit;
import features.interfaces.Numeric;
import users.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DepositService implements IDatabaseOperations<Deposit> {
    private static DepositService depositServiceInstance = null;
    List<Deposit> clientDeposits;
    Deposit selectedDeposit;
    HashMap<String, Double> typeDobanda;
    private static Scanner scan = new Scanner(System.in);
    private final int DEPOSITS_LIMIT = 10;
    private final Double MAX_DEPOSIT_BALANCE = 100000000.0;
    private Integer lastId = 0;

    private DepositService() { setNull(); }

    public static DepositService getInstance() {
        if (depositServiceInstance == null)
            depositServiceInstance = new DepositService();
        return depositServiceInstance;
    }

//    public DepositService(HashMap<String, Double> typeDobanda) {
//        setNull();
//        this.typeDobanda = typeDobanda;
//    }

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
        Deposit newDeposit = new Deposit(lastId++, cls.getCurrentClient(), currency, dobanda, type, balance);
        clientDeposits.add(newDeposit);
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
    public Deposit toObjectFromDB(String[] dbRow, Object... services) {
        ClientService cls = null;
        CurrencyService crs = null;
        if (services[0] instanceof ClientService) cls = (ClientService) services[0];
        if (services[1] instanceof CurrencyService) crs = (CurrencyService) services[1];
        int id = Integer.parseInt(dbRow[0]);
        Integer clientID = Integer.parseInt(dbRow[1]);
        Integer currencyID = Integer.parseInt(dbRow[2]);
        Client client = cls.getElementById(clientID);
        Currency currency = crs.getElementById(currencyID);
        Double dobanda = Double.parseDouble(dbRow[3]);
        String type = dbRow[4];
        Double sumaDepusa = Double.parseDouble(dbRow[5]);
        return new Deposit(id, client, currency, dobanda, type, sumaDepusa);
    }

    @Override
    public String[] toDBString(Deposit obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getClient().getID().toString(),
                obj.getAccountCurrency().getID().toString(),
                obj.getDobanda().toString(),
                obj.getType(),
                obj.getSumaDepusa().toString()
        };
    }

    @Override
    public Deposit getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Deposit> maybeDeposit =
                clientDeposits.stream()
                        .filter(deposit -> deposit.getID().equals(id))
                        .findFirst();
        return maybeDeposit.orElse(null);
    }
}
