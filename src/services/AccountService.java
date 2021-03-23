package services;

import features.Account;
import features.Currency;
import features.interfaces.Numeric;
import users.Client;

import java.util.List;
import java.util.Scanner;

public class AccountService {
    private List<Account> clientAccounts;
    private Account selectedAccount;

    public AccountService() {
        clientAccounts = null;
        selectedAccount = null;
    }

    public void setClientAccounts(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientAccounts = client.getAccounts();
    }

    public void selectAccount(int index) throws Exception {
        int size = clientAccounts.size();
        if (1 <= index && index <= size) selectedAccount = clientAccounts.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un cont cu asa numar");
    }

    public void openAccount(ClientService cls, CurrencyService crs) throws Exception {
        Scanner scan = new Scanner(System.in);
        String line;
        int accountsNumber = clientAccounts.size();
        if (accountsNumber > 10) {
            throw new Exception("Aveti deja limita admisibila de conturi deschise. Inchideti unele din cele curente pentru a deschide unul nou");
        }
        Currency currency = crs.selectCurrency();
        System.out.println("Introduceti suma de bani: ");
        Double balance = 0.0;
        while (true) {
            line = scan.nextLine();
            if (Numeric.isNumeric(line)) {
                balance = Double.parseDouble(line);
                if (balance > 1000000.0) {
                    System.out.println("Suma introdusa depaseste limita admisibila");
                } else if (balance < 0.0) {
                    System.out.println("Valoarea introdusa este una negativa. Introduceti o valoare pozitiva");
                } else {
                    break;
                }
            }
            else
                System.out.println("Introduceti o valoare numerica pozitiva");
        }
        Account newAccount = new Account(cls.getCurrentClient(), currency, balance);
        clientAccounts.add(newAccount);
        System.out.println("Operatiune realizata cu succes. Doriti sa fie eliberat si un card bancar pentru contul creat? (y/n)");
        while (true) {
            line = scan.nextLine();
            if (line.equals("y") || line.equals("yes")) {
                newAccount.emiteCard();
            } else if (line.equals("n") || line.equals("no")) {
                return;
            } else {
                System.out.println("Introduceti un raspuns valid: y/n");
            }
        }
    }

    public void printExtrasDeCont() {
        if (selectedAccount != null) selectedAccount.extrasDeCont();
        else System.out.println("Selectati un cont pentru a afisa extrasul");
    }

    public void addBalance(Double amount) {
        if (selectedAccount != null) selectedAccount.addMoney(amount);
        else System.out.println("Selectati un cont pentru a putea adauga bani pe el");
    }

    public void withdrawBalance(Double amount) {
        if (selectedAccount != null) {
            try {
                selectedAccount.withdrawMoney(amount);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else System.out.println("Selectati un cont pentru a putea extrage bani de pe el");
    }
}
