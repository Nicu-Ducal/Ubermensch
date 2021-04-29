package features;

import features.interfaces.Rate;
import users.Client;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Contract implements Rate {
    protected Integer id;
    protected Client client;
    protected LocalDateTime creationDate;
    protected Currency accountCurrency;
    protected Double dobanda;                     // gain
    protected final String type;                  // Can be of 3 types: Lunar, Anual or Cincinal
    protected LocalDateTime lastBalanceUpdate;
    protected String status;

    public Contract(Integer id, Client client, Currency accountCurrency, Double dobanda, String type, String status) {
        this.id = id;
        this.client = client;
        this.accountCurrency = accountCurrency;
        this.creationDate = LocalDateTime.now();
        this.lastBalanceUpdate = LocalDateTime.now();
        this.dobanda = dobanda;
        this.status = status;
        this.type = type;
    }

    public Integer getID() { return id; }

    public Client getClient() {
        return client;
    }

    public LocalDateTime getCreationDate() { return creationDate; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract)) return false;
        Contract contract = (Contract) o;
        return id.equals(contract.id) && client.equals(contract.client) && creationDate.equals(contract.creationDate) && accountCurrency.equals(contract.accountCurrency) && dobanda.equals(contract.dobanda) && type.equals(contract.type) && lastBalanceUpdate.equals(contract.lastBalanceUpdate) && status.equals(contract.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, creationDate, accountCurrency, dobanda, type, lastBalanceUpdate, status);
    }

    public abstract void extras();
}
