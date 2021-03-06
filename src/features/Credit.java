package features;

import users.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Credit extends Contract implements Comparable<Credit> {
    private Double sumaImprumutata;
    private Double sumaRestituita;
    private Double sumaDeRestituit;
    private LocalDateTime closingDate;

    public Credit(Integer id, Client client, Currency accountCurrency, Double dobanda, String type, Double sumaImprumutata, Double sumaRestituita, LocalDateTime creationDate) {
        super(id, client, accountCurrency, dobanda, type, "Open", creationDate);
        this.sumaImprumutata = sumaImprumutata;
        this.sumaRestituita = sumaRestituita;
        this.sumaDeRestituit = sumaImprumutata * (1 + dobanda / 100.0);
    }

    public Double getSumaImprumutata() {
        return sumaImprumutata;
    }

    public Double getSumaRestituita() {
        return sumaRestituita;
    }

    private void setSumaRestituita(Double sumaRestituita) {
        this.sumaRestituita = sumaRestituita;
    }

    public Double getSumaDeRestituit() {
        return sumaDeRestituit;
    }

    private void setSumaDeRestituit(Double sumaDeRestituit) {
        this.sumaDeRestituit = sumaDeRestituit;
    }

    public LocalDateTime getClosingDate() {
        return closingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credit)) return false;
        if (!super.equals(o)) return false;
        Credit credit = (Credit) o;
        return sumaImprumutata.equals(credit.sumaImprumutata) && sumaRestituita.equals(credit.sumaRestituita) && sumaDeRestituit.equals(credit.sumaDeRestituit) && closingDate.equals(credit.closingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sumaImprumutata, sumaRestituita, sumaDeRestituit, closingDate);
    }

    @Override
    public int compareTo(Credit o) {
        if (sumaDeRestituit.equals(o.getSumaDeRestituit())) return 0;
        else if (sumaDeRestituit > o.getSumaDeRestituit()) return -1;
        return 1;
    }

    @Override
    public String toString() {
        return "Credit bancar, client: " + client.getName() + ", Suma imprumutata: " + sumaImprumutata + ", Valuta: " + accountCurrency.getFullName();
    }

    @Override
    public void extras() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm");
        System.out.println("Extras pentru creditul clientului " + client.getName() + " de la data de " + ft.format(now));
        System.out.println("Suma imprumutata: " + sumaImprumutata + " " + accountCurrency.getName() + " (" + accountCurrency.getInternational() + ")");
        System.out.println("Suma restituita pana acum: " + sumaRestituita + " " + accountCurrency.getName() + " (" + accountCurrency.getInternational() + ")");
        System.out.println("Suma totala pentru restituire: " + sumaDeRestituit + " " + accountCurrency.getName() + " (" + accountCurrency.getInternational() + ")");
        System.out.println("Rata dobanzii: " + dobanda + "%");
        System.out.println("Tipul creditului: " + type);
        // Tranzactii
    }

    public void payCredit(Double amount) {
        sumaRestituita += amount;
    }

    public Boolean closeCredit(Double amount) {
        if (sumaDeRestituit <= sumaRestituita) {
            setStatus("Closed");
            return true;
        }
        return false;
    }

    @Override
    public Boolean shouldUpdateBalance() {
        if (status.equals("Closed"))
            return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last;
        if (type.equals("Lunar")) {
            last = lastBalanceUpdate.plusMonths(1);
        } else if (type.equals("Anual")) {
            last = lastBalanceUpdate.plusYears(1);
        } else {
            last = lastBalanceUpdate.plusYears(5);
        }

        return !last.isAfter(now);
    }

    @Override
    public void updateAccountBalance() {
        if (shouldUpdateBalance()) {
            while (shouldUpdateBalance()) {
                sumaDeRestituit += sumaDeRestituit * dobanda;
                if (type.equals("Lunar")) {
                    setLastBalanceUpdate(lastBalanceUpdate.plusMonths(1));
                } else if (type.equals("Anual")) {
                    setLastBalanceUpdate(lastBalanceUpdate.plusYears(1));
                } else {
                    setLastBalanceUpdate(lastBalanceUpdate.plusYears(5));
                }
            }
        }
    }
}
