package app;

import audit.AuditService;
import database.Database;
import services.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.Scanner;

public class BankApp {
    private static BankApp bank = null;
    private static Scanner scan = new Scanner(System.in);

    private Database db;
    private AuditService auditService;
    private ClientService clientService;
    private AccountService accountService;
    private CurrencyService currencyService;
    private DepositService depositService;
    private CreditService creditService;
    private TransactionService transactionService;
    private Boolean running;
    private Clip music;

    private BankApp() {
        db = Database.getInstance();
        auditService = AuditService.getInstance();
        clientService = ClientService.getInstance();
        accountService = AccountService.getInstance();
        currencyService = CurrencyService.getInstance();
        depositService = DepositService.getInstance();
        creditService = CreditService.getInstance();
        transactionService = TransactionService.getInstance();
        running = true;
    }

    public static BankApp getInstance() {
        if (bank == null)
            bank = new BankApp();
        return bank;
    }

    private Clip playMusic() {
        File musicPath = new File("src/assets/theme.wav");
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volume.getMaximum() - volume.getMinimum();
            float gain = (range * (float) 0.5) + volume.getMinimum();
            volume.setValue(gain);
            clip.start();
            return clip;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void stopMusic(Clip clip) {
        clip.stop();
    }

    private boolean executeCommand(String cmd) {
        Boolean isLoggedIn = clientService.getCurrentClient() != null;
        if (cmd.equals("exit")) {
            running = false;
            System.out.println("Thanks for using the app!");
        } else if (cmd.equals("login")) {
            clientService.LogIn();
        } else if (cmd.equals("logout")) {
            clientService.LogOut();
        } else if (cmd.equals("register")) {
            clientService.Register();
        } else if (cmd.equals("change-user-account")) {
            clientService.ChangeAccount();
        } else if (cmd.equals("delete-user-account")) {
            clientService.DeleteAccount();
        } else if (cmd.equals("show-info")) {
            clientService.printClientInfo();
        } else if (cmd.split(" ")[0].equals("show") && cmd.split(" ").length == 2) {
            String type = cmd.split(" ")[1];
            switch (type) {
                case "accounts":
                    accountService.showAccountsInfo();
                    break;
                case "deposits":
                    depositService.showDepositsInfo();
                    break;
                case "credits":
                    creditService.showCreditsInfo();
                    break;
                default:
                    System.out.println("Invalid command");
            }
        } else if (cmd.split(" ")[0].equals("open") && cmd.split(" ").length == 2) {
            String type = cmd.split(" ")[1];
            try {
                switch (type) {
                    case "account":
                        accountService.openAccount(clientService, currencyService);
                        break;
                    case "deposit":
                        depositService.openDeposit(clientService, currencyService);
                        break;
                    case "credit":
                        creditService.openCredit(clientService, currencyService);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (cmd.split(" ")[0].equals("close") && cmd.split(" ").length == 2) {
            String type = cmd.split(" ")[1];
            switch (type) {
                case "account":
                    accountService.closeAccount();
                    break;
                case "deposit":
                    depositService.closeDeposit();
                    break;
                case "credit":
                    creditService.closeCredit();
                    break;
                default:
                    System.out.println("Invalid command");
            }
        } else if (cmd.split(" ")[0].equals("select") && cmd.split(" ").length == 3) {
            String type = cmd.split(" ")[1];
            String index = cmd.split(" ")[2];
            switch (type) {
                case "account":
                    accountService.selectAccount(index);
                    break;
                case "deposit":
                    depositService.selectDeposit(index);
                    break;
                case "credit":
                    creditService.selectCredit(index);
                    break;
                default:
                    System.out.println("Invalid command");
            }
        } else if (cmd.equals("show-account-info")) {
            accountService.printExtrasDeCont();
        } else if (cmd.equals("show-deposit-info")) {
            depositService.informatiiDepozit();
        } else if (cmd.equals("show-credit-info")) {
            creditService.informatiiCredit();
        } else if (cmd.startsWith("add-money") && cmd.split(" ")[0].equals("add-money") && cmd.split(" ").length == 3) {
            String amount = cmd.split(" ")[1];
            String valuta = cmd.split(" ")[2];
            accountService.addBalance(amount, valuta, currencyService);
        } else if (cmd.startsWith("withdraw-money") && cmd.split(" ")[0].equals("withdraw-money") && cmd.split(" ").length == 3) {
            String amount = cmd.split(" ")[1];
            String valuta = cmd.split(" ")[2];
            accountService.withdrawBalance(amount, valuta, currencyService);
        } else if (cmd.startsWith("pay-credit") && cmd.split(" ")[0].equals("pay-credit") && cmd.split(" ").length == 3) {
            String amount = cmd.split(" ")[1];
            String valuta = cmd.split(" ")[2];
            creditService.payCredit(amount, valuta);
        } else if (cmd.equals("transfer-money")) {
            transactionService.makeTransaction(clientService, accountService, currencyService);
        } else if (cmd.equals("show-transfers")) {
            transactionService.printTransactions();
        } else if (cmd.equals("play-theme")) {
            music = playMusic();
        } else if (cmd.equals("stop-theme")) {
            stopMusic(music);
        } else {
            System.out.println("Invalid command");
            return false;
        }
        return true;
    }

    public void run() {
        // Load the database
        db.loadDatabase();
        System.out.println("Welcome to Ubermensch. Log in or register a new account to continue");
        String appName = "Ubermensch>";
        String loggedInName;
        String action;
        while (running) {
            loggedInName = clientService.getCurrentClient() != null ? clientService.getCurrentClient().getUsername() + ">" : "";
            System.out.print(appName + loggedInName + " ");
            action = scan.nextLine().trim();
            boolean done = executeCommand(action);
            if (done) auditService.LogAction(action);
        }
    }
}
