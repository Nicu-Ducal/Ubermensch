package features.interfaces;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

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
                    return RoundTwoDecimals(balance);
                }
            }
            else
                System.out.println("Introduceti o valoare numerica pozitiva");
        }
    }

    static Long RandomNumber(Long MIN_VALUE, Long MAX_VALUE) {
        return ThreadLocalRandom.current().nextLong(MIN_VALUE, MAX_VALUE + 1);
    }

    static Integer RandomNumber(Integer MIN_VALUE, Integer MAX_VALUE) {
        return ThreadLocalRandom.current().nextInt(MIN_VALUE, MAX_VALUE + 1);
    }

    static Double RoundTwoDecimals(Double number) {
        return Math.round(number * 100) / 100.0;
    }
}
