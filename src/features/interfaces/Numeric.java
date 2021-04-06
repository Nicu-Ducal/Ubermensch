package features.interfaces;

import java.util.Scanner;

public interface Numeric {
    static Boolean isNumeric(String num) {
        if (num == null) return false;
        try {
            Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static Boolean isInteger(String num) {
        if (num == null) return false;
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static Double getBalance(Scanner scan, Double MAX_VALUE) {
        String line;
        while (true) {
            line = scan.nextLine();
            if (Numeric.isNumeric(line)) {
                Double balance = Double.parseDouble(line);
                if (balance > MAX_VALUE) {
                    System.out.println("Suma introdusa depaseste limita admisibila");
                } else if (balance < 0.0) {
                    System.out.println("Valoarea introdusa este una negativa. Introduceti o valoare pozitiva");
                } else {
                    return balance;
                }
            }
            else
                System.out.println("Introduceti o valoare numerica pozitiva");
        }
    }
}
