package services;

import database.IDatabaseOperations;
import features.Account;
import features.CreditCard;
import features.Currency;
import features.interfaces.Numeric;
import users.Client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AccountService implements IDatabaseOperations<Account> {
    private static AccountService accountServiceInstance = null;
    private List<Account> accounts;
    private List<Account> clientAccounts;
    private Account selectedAccount;
    private static Scanner scan = new Scanner(System.in);
    private final int ACCOUNTS_LIMIT = 10;
    private final Double MAX_ACCOUNT_BALANCE = 1000000.0;

    private AccountService() {
        setNull();
    }

    public static AccountService getInstance() {
        if (accountServiceInstance == null)
            accountServiceInstance = new AccountService();
        return accountServiceInstance;
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
        if (accountsNumber > ACCOUNTS_LIMIT) {
            throw new Exception("Aveti deja limita admisibila de conturi deschise. Inchideti unele din cele curente pentru a deschide unul nou");
        }
        Currency currency = crs.selectCurrency();
        System.out.print("Introduceti suma de bani: ");
        Double balance = Numeric.getBalance(scan, MAX_ACCOUNT_BALANCE);

        Integer id = accounts.size() == 0 ? 1 : accounts.get(accounts.size() - 1).getID() + 1;
        Account newAccount = new Account(id, cls.getCurrentClient(), currency, balance);
        clientAccounts.add(newAccount);
        accounts.add(newAccount);
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

    public void closeAccount() {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa fiti logat si sa aveti un cont selectat pentru a-l sterge");
            return;
        }
        if (selectedAccount == null) {
            System.out.println("Selectati un cont pentru a-l sterge");
            return;
        }
        clientAccounts.remove(selectedAccount);
        accounts.remove(selectedAccount);
        selectedAccount = null;
        System.out.println("Contul a fost sters cu succes");
    }

    public Account getSelectedAccount() {
        return selectedAccount;
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

    public void addBalance(String amount, String currency, CurrencyService crs) {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a efectua aceasta operatie");
            return;
        }
        if (selectedAccount != null) {
            Currency cr = crs.getCurrency(currency);
            if (cr == null) {
                System.out.println("Valuta introdusa de dvs. nu exista");
                return;
            }
            if (Numeric.isNumeric(amount) && Double.parseDouble(amount) > 0.0) {
                Double moneyAmount = Numeric.RoundTwoDecimals(Double.parseDouble(amount));
                // Daca nu e valuta contului nostru, facem schimbul valutar
                try {
                    moneyAmount = crs.convert(moneyAmount, cr, selectedAccount.getAccountCurrency());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
                selectedAccount.addMoney(moneyAmount);
                System.out.println("Contul dvs. a fost suplinit cu " + moneyAmount + " " + selectedAccount.getAccountCurrency().getName());
            } else
                System.out.println("Introduceti o valoare numerica pozitiva pentru suplinirea contului");
        }
        else System.out.println("Selectati un cont pentru a putea adauga bani pe el");
    }

    public void withdrawBalance(String amount, String currency, CurrencyService crs) {
        if (clientAccounts == null) {
            System.out.println("Trebuie sa va logati mai intai pentru a efectua aceasta operatie");
            return;
        }

        if (selectedAccount != null) {
            Currency cr = crs.getCurrency(currency);
            if (cr == null) {
                System.out.println("Valuta introdusa de dvs. nu exista");
                return;
            }
            if (Numeric.isNumeric(amount) && Double.parseDouble(amount) > 0.0) {
                Double moneyAmount = Numeric.RoundTwoDecimals(Double.parseDouble(amount));
                try {
                    moneyAmount = crs.convert(moneyAmount, cr, selectedAccount.getAccountCurrency());
                    selectedAccount.withdrawMoney(moneyAmount);
                    System.out.println("Ati retras de pe cont " + moneyAmount + " " + selectedAccount.getAccountCurrency().getName());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Introduceti o valoare numerica pozitiva pentru retragere");
            }
        }
        else System.out.println("Selectati un cont pentru a putea extrage bani de pe el");
    }

    public Account selectAccountFromClient(Client cl) {
        List<Account> accounts = cl.getAccounts();
        if (accounts.size() == 0) {
            System.out.println("Clientul selectat nu are nici un cont bancar");
            return null;
        }

        System.out.println("Selectati unul din conturile clientului " + cl.getName());
        for (int i = 0; i < accounts.size(); i++)
            System.out.println((i + 1) + ") Contul " + (i + 1) + ", Valuta: " + accounts.get(i).getAccountCurrency().getFullName());
        while (true) {
            int idx = scan.nextInt();
            if (1 <= idx && idx <= accounts.size())
                return accounts.get(idx - 1);
            System.out.println("Introduceti un numar valid");
        }
    }

    /*
        Database related operations
     */

    @Override
    public List<Account> getCollection() { return accounts; }

    @Override
    public void load(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public Account toObjectFromDB(String[] dbRow) {
        Integer id = Integer.parseInt(dbRow[0]);
        LocalDateTime creationDate = LocalDateTime.parse(dbRow[1]);
        Double balance = Double.parseDouble(dbRow[2]);
        Integer clientID = Integer.parseInt(dbRow[3]);
        Integer currencyID = Integer.parseInt(dbRow[4]);
        CreditCard creditCard = null;
        Client client = ClientService.getInstance().getElementById(clientID);
        Currency currency = CurrencyService.getInstance().getElementById(currencyID);
        return new Account(id, creationDate, balance, client, currency, creditCard);
    }

    @Override
    public String[] toDBString(Account obj) {
        return new String[] {
                obj.getID().toString(),
                obj.getCreationDate().toString(),
                obj.getBalance().toString(),
                obj.getClient().getID().toString(),
                obj.getAccountCurrency().getID().toString(),
                null
        };
    }

    @Override
    public Account getElementById(Integer id) {
        /*
         Ceva similiar cu Maybe din Haskell, daca gaseste un client, atunci pastreaza referinta lui, daca nu, pastreaza null,
         exact ca si Just si Nothing
        */
        Optional<Account> maybeAccount =
                accounts.stream()
                        .filter(account -> account.getID().equals(id))
                        .findFirst();
        return maybeAccount.orElse(null);
    }

    public List<Account> getAccountsByClientId(Integer clientID) {
        List<Account> clAccounts =
                accounts.stream()
                .filter(account -> account.getClient().getID().equals(clientID))
                .collect(Collectors.toList());
        return clAccounts;
    }
}
