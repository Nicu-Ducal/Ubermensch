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
    private static Scanner scan = new Scanner(System.in);

    public AccountService() {
        clientAccounts = null;
        selectedAccount = null;
    }

    public void setClientAccounts(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientAccounts = client.getAccounts();
    }

    public void setNull() {
        clientAccounts = null;
        selectedAccount = null;
    }

    public void selectAccount(String index) {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a selecta un cont");
            return;
        } else if (clientAccounts.size() == 0) {
            System.out.println("Nu aveti nici un cont bancar");
            return;
        }
        try {
            if (Numeric.isInteger(index)) {
                selectAccountWithIndex(Integer.parseInt(index));
                System.out.println("Contul a fost selectat cu succes");
            } else {
                System.out.println("Introduceti o valoare intreaga");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void selectAccountWithIndex(int index) throws Exception {
        int size = clientAccounts.size();
        if (1 <= index && index <= size) selectedAccount = clientAccounts.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un cont cu asa numar");
    }

    public void openAccount(ClientService cls, CurrencyService crs) throws Exception {
        if (cls.getCurrentClient() == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a deschide un cont");
            return;
        }
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
                System.out.println("Cardul a fost eliberat cu succes. Puteti sa selectati acum ");
                return;
            } else if (line.equals("n") || line.equals("no")) {
                return;
            } else {
                System.out.println("Introduceti un raspuns valid: y/n");
            }
        }
    }

    public void showAccountsInfo() {
        if (clientAccounts == null) {
            System.out.println("Nu sunteti logat pentru a afisa informatiile despre conturile bancare");
        } else if (clientAccounts.size() == 0) {
            System.out.println("Nu aveti nici un cont bancar deschis");
        } else {
            System.out.println("Conturile bancare: ");
            for (int i = 0; i < clientAccounts.size(); i++)
                System.out.println((i + 1) + ") " + clientAccounts.get(i));
        }
    }

    public void printExtrasDeCont() {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a efectua aceasta operatie");
            return;
        }
        if (selectedAccount != null) selectedAccount.extrasDeCont();
        else System.out.println("Selectati un cont pentru a afisa extrasul");
    }

    public void addBalance(String amount) {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a efectua aceasta operatie");
            return;
        }
        if (selectedAccount != null) {
            if (Numeric.isNumeric(amount) && Double.parseDouble(amount) > 0.0) {
                Double mamount = Double.parseDouble(amount);
                selectedAccount.addMoney(mamount);
                System.out.println("Contul dvs. a fost suplinit cu " + mamount + " " + selectedAccount.getAccountCurrency().getName());
            } else
                System.out.println("Introduceti o valoare numerica pozitiva pentru suplinirea contului");
        }
        else System.out.println("Selectati un cont pentru a putea adauga bani pe el");
    }

    public void withdrawBalance(String amount) {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a efectua aceasta operatie");
            return;
        }

        if (selectedAccount != null) {
            if (Numeric.isNumeric(amount) && Double.parseDouble(amount) > 0.0) {
                Double mamount = Double.parseDouble(amount);
                try {
                    selectedAccount.withdrawMoney(mamount);
                    System.out.println("Ati retras de pe cont " + mamount + " " + selectedAccount.getAccountCurrency().getName());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Introduceti o valoare numerica pozitiva pentru retragere");
            }
        }
        else System.out.println("Selectati un cont pentru a putea extrage bani de pe el");
    }
}
