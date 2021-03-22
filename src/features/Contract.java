package features;

import users.Client;

import java.time.LocalDateTime;

public abstract class Contract {
    protected Client client;
    protected LocalDateTime creationDate;
    protected Currency accountCurrency;
    protected Double dobanda;                     // gain
    protected final String type;                  // Can be of 3 types: Lunar, Anual or Cincinal
    protected LocalDateTime lastBalanceUpdate;
    protected String status;

    public Contract(Client client, Currency accountCurrency, Double dobanda, String type, String status) {
        this.client = client;
        this.accountCurrency = accountCurrency;
        this.creationDate = LocalDateTime.now();
        this.lastBalanceUpdate = LocalDateTime.now();
        this.dobanda = dobanda;
        this.status = status;
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public LocalDateTime getCreationDate() {

        return creationDate;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    public Double getDobanda() {
        return dobanda;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getLastBalanceUpdate() {
        return lastBalanceUpdate;
    }

    public void setLastBalanceUpdate(LocalDateTime lastBalanceUpdate) {
        this.lastBalanceUpdate = lastBalanceUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void extras();
}
