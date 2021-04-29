package services;

import database.IDatabaseOperations;
import features.Credit;
import features.Currency;
import features.interfaces.Numeric;
import users.Client;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CreditService implements IDatabaseOperations<Credit> {
    private static CreditService creditServiceInstance = null;
    List<Credit> clientCredits;
    Credit selectedCredit;
    HashMap<String, Double> typeDobanda;
    private final Double MAX_CREDIT_AMOUNT = 50000000.0;
    private final int CREDITS_LIMIT = 5;
    private static Scanner scan = new Scanner(System.in);
    private Integer lastId = 0;


    private CreditService() { setNull(); }

    public static CreditService getInstance() {
        if (creditServiceInstance == null)
            creditServiceInstance = new CreditService();
        return creditServiceInstance;
    }

//    public CreditService(HashMap<String, Double> typeDobanda) {
//        setNull();
//        this.typeDobanda = typeDobanda;
//    }

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
        Credit newCredit = new Credit(lastId++, cls.getCurrentClient(), currency, dobanda, type, balance, 0.0);
        clientCredits.add(newCredit);
        System.out.println("Creditul a fost deschis cu succes!");
    }

    public void closeCredit() {
        if (clientCredits == null) {
            System.out.println("Trebuie sa fiti logat si sa aveti un Creditul selectat pentru a-l sterge");
            return;
        }
        if (selectedCredit == null) {
            System.out.println("Selectati un depozit pentru a-l sterge");
            return;
        }
        clientCredits.remove(selectedCredit);
        selectedCredit = null;
        System.out.println("Creditul a fost sters cu succes");
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
    public Credit toObjectFromDB(String[] dbRow, Object... services) {
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
        Double sumaImprumutata = Double.parseDouble(dbRow[5]);
        Double sumaRestituita = Double.parseDouble(dbRow[6]);
        return new Credit(id, client, currency, dobanda, type, sumaImprumutata, sumaRestituita);
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
                obj.getSumaRestituita().toString()
        };
    }

    @Override
    public Credit getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Credit> maybeCredit =
                clientCredits.stream()
                        .filter(credit -> credit.getID().equals(id))
                        .findFirst();
        return maybeCredit.orElse(null);
    }
}
