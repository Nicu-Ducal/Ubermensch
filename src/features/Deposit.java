package features;

import users.Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Deposit extends Contract implements Comparable<Deposit> {
    private LocalDateTime expirationDate;
    private Double sumaDepusa;

    public Deposit(Client client, Currency accountCurrency, Double dobanda, String type, Double sumaDepusa) {
        super(client, accountCurrency, dobanda, type, "Pending approval. Wait for an employee to approve your request");
        this.sumaDepusa = sumaDepusa;
        this.expirationDate = this.creationDate.plusYears(1);
    }

    public Double getSumaDepusa() {
        return sumaDepusa;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deposit)) return false;
        if (!super.equals(o)) return false;
        Deposit deposit = (Deposit) o;
        return expirationDate.equals(deposit.expirationDate) && sumaDepusa.equals(deposit.sumaDepusa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), expirationDate, sumaDepusa);
    }

    @Override
    public int compareTo(Deposit o) {
        if (sumaDepusa.equals(o.getSumaDepusa())) return expirationDate.compareTo(o.getExpirationDate());
        else if (sumaDepusa > o.getSumaDepusa()) return -1;
        return 1;
    }

    @Override
    public void extras() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter ft = DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm");
        System.out.println("Extras pentru depositul clientului " + client.getName() + " de la data de " + ft.format(now));
        System.out.println("Soldul depositului: " + sumaDepusa + " " + accountCurrency.getName() + " (" + accountCurrency.getInternational() + ")");
        System.out.println("Rata dobanzii: " + dobanda * 100 + "%");
        System.out.println("Tip deposit: " + type);
        // Tranzactii
    }

    public String checkDepositStatus() {
        if (expirationDate.compareTo(LocalDateTime.now()) > 0) {
            return "Depozitul inca este deschis si valabil";
        } else if (expirationDate.compareTo(LocalDateTime.now()) == 0) {
            return "Depozitul expira astazi";
        } else {
            if (!this.getStatus().equals("Expired"))
                this.setStatus("Expired");
            return "Depozitul a expirat. Actualizati-l sau ridicati banii";
        }
    }

    @Override
    public Boolean shouldUpdateBalance() {
        if (status.equals("Expired"))
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
                sumaDepusa += sumaDepusa * dobanda;
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
