package app;

public class BankApp {
    private static BankApp bank = null;

    private BankApp() {}

    public static BankApp getInstance() {
        if (bank == null)
            bank = new BankApp();
        return bank;
    }

    public void run() {

    }
}
