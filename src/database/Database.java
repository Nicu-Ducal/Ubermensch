package database;

import features.Currency;
import users.Client;
import users.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    public Database() {}

    public List<Client> createClients() {
        List<Client> db = new ArrayList<>();
        db.add(new Client(1, "nicu-ducal", "parola", "Nicu Ducal", "nicu@gmail.com", "Fizica"));
        db.add(new Client(1, "user-doi", "123445", "Utilizator 2", "user2@gmail.com", "Juridica"));
        db.add(new Client(1, "melon_usk", "tesla", "Melon Usk", "tesla@yahoo.com", "Juridica"));
        db.add(new Client(1, "beffjezos", "amazon", "Beff Jezos", "beff@amazon.com", "Fizica"));
        db.add(new Client(1, "tonalt.drump", "maga2020", "Tonalt Drump", "donaldduck@gmail.com", "Fizica"));
        return db;
    }

    public List<Employee> createEmployees() {
        List<Employee> db = new ArrayList<>();
        db.add(new Employee(1, "angajat1", "parola1", "Seful firmei", "boss@gmail.com", "Manager"));
        db.add(new Employee(2, "angajat2", "parola2", "Anagajatul lunii", "bestworker@gmail.com", "Angajat"));
        db.add(new Employee(3, "angajat3", "parola3", "Angajatul anului", "bestyearworker@gmail.com", "Angajat"));
        return db;
    }

    public List<Currency> createCurrencies() {
        List<Currency> db = new ArrayList<>();
        db.add(new Currency("Lei", "RON"));
        db.add(new Currency("Euro", "EUR"));
        db.add(new Currency("Dolari", "USD"));
        db.add(new Currency("Lire Sterline", "GBP"));
        return db;
    }

    public HashMap<String, Double> createDepositDobanda() {
        HashMap<String, Double> db = new HashMap<>();
        db.put("Lunar", 2.0);
        db.put("Anual", 5.0);
        db.put("Cincinal", 10.0);
        return db;
    }

    public HashMap<String, Double> createCreditDobanda() {
        HashMap<String, Double> db = new HashMap<>();
        db.put("Lunar", 2.0);
        db.put("Anual", 15.0);
        db.put("Cincinal", 40.0);
        return db;
    }
}
