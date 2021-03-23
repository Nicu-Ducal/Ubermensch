package services;

import features.Deposit;
import users.Client;

import java.util.List;

public class DepositService {
    List<Deposit> clientDeposits;
    Deposit selectedDeposit;

    public DepositService() {
        this.clientDeposits = null;
        selectedDeposit = null;
    }

    public void setClientDeposits(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientDeposits = client.getDeposits();
    }

    public void selectDeposit(int index) throws Exception {
        int size = clientDeposits.size();
        if (1 <= index && index <= size) selectedDeposit = clientDeposits.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un deposit cu asa numar");
    }

    public void printExtrasDeCont() {
        if (selectedDeposit != null) selectedDeposit.extras();
        else System.out.println("Selectati un cont pentru a afisa extrasul");
    }

    // TODO: OpenDeposit si CloseDeposit
}
