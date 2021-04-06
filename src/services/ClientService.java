package services;

import users.Client;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ClientService {
    private List<Client> clientList;
    HashSet<String> usernames;
    HashSet<String> emails;
    private Client currentClient;
    private static Scanner scan = new Scanner(System.in);

    public ClientService(List<Client> clientList) {
        this.clientList = clientList;
        this.currentClient = null;
        usernames = new HashSet<>();
        emails = new HashSet<>();
        for (Client cl: clientList) {
            usernames.add(cl.getUsername());
            emails.add(cl.getEmail());
        }
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public void LogIn(AccountService as, DepositService ds, CreditService cs) {
        if (currentClient != null) {
            System.out.println("You are already logged in with an account");
            return;
        }
        System.out.print("Introduceti username-ul: ");
        String username = scan.nextLine();
        System.out.print("Introduceti parola: ");
        String password = scan.nextLine();
        for (Client cl: clientList) {
            if (cl.getUsername().equals(username) && cl.checkPassword(password)) {
                currentClient = cl;
                try {
                    as.setClientAccounts(cl);
                    ds.setClientDeposits(cl);
                    cs.setClientCredits(cl);
                    System.out.println("V-ati logat cu succes");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return;
            }
        }
        System.out.println("Nu exista nici un client cu aceasta combinatie de username si parola");
    }

    public void LogOut(AccountService as, DepositService ds, CreditService cs) {
        if (currentClient != null) {
            currentClient = null;
            as.setNull();
            ds.setNull();
            cs.setNull();
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
        clientList.add(new Client(clientList.size() + 1, username, password, name, email, tip));
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
}
