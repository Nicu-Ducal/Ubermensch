package features.interfaces;

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
}
