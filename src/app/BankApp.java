package app;

import database.Database;
import services.*;

import java.util.Scanner;

public class BankApp {
    private static BankApp bank = null;
    private static Scanner scan = new Scanner(System.in);

    ClientService clientService;
    AccountService accountService;
    CurrencyService currencyService;
    DepositService depositService;
    CreditService creditService;
    Boolean running;

    private BankApp() {
        Database db = new Database();
        clientService = new ClientService(db.createClients());
        accountService = new AccountService();
        currencyService = new CurrencyService(db.createCurrencies());
        running = true;
    }

    public static BankApp getInstance() {
        if (bank == null)
            bank = new BankApp();
        return bank;
    }

    private void executeCommand(String cmd) {
        Boolean isLoggedIn = clientService.getCurrentClient() != null;
        if (cmd.equals("exit")) {
            running = false;
            System.out.println("Thanks for using the app!");
        } else if (cmd.equals("login")) {
            clientService.LogIn(accountService);
        } else if (cmd.equals("logout")) {
            clientService.LogOut(accountService);
        } else if (cmd.equals("register")) {
            clientService.Register();
        } else if (cmd.equals("show-info")) {
            clientService.printClientInfo();
        } else if (cmd.equals("show-accounts")) {
            accountService.showAccountsInfo();
        } else if (cmd.equals("open-account")) {
            try {
                accountService.openAccount(clientService, currencyService);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (cmd.startsWith("select-account") && cmd.split(" ")[0].equals("select-account")) {
            String index = cmd.split(" ")[1];
            accountService.selectAccount(index);
        } else if (cmd.equals("show-account-info")) {
            accountService.printExtrasDeCont();
        } else if (cmd.startsWith("add-money") && cmd.split(" ")[0].equals("add-money")) {
            String amount = cmd.split(" ")[1];
            accountService.addBalance(amount);
        } else if (cmd.startsWith("withdraw-money") && cmd.split(" ")[0].equals("withdraw-money")) {
            String amount = cmd.split(" ")[1];
            accountService.withdrawBalance(amount);
        } else {
            System.out.println("Invalid command");
        }
    }

    public void run() {
        System.out.println("Welcome to Ubermensch. Log in or register a new account to continue");
        String appName = "Ubermensch>";
        String loggedInName;
        String input;
        while (running) {
            loggedInName = clientService.getCurrentClient() != null ? clientService.getCurrentClient().getUsername() + ">" : "";
            System.out.print(appName + loggedInName + " ");
            input = scan.nextLine();
            executeCommand(input);
        }
    }
}
