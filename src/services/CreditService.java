package services;

import features.Credit;
import users.Client;

import java.util.List;

public class CreditService {
    List<Credit> clientCredits;
    Credit selectedCredit;

    public CreditService() {
        this.clientCredits = null;
        selectedCredit = null;
    }

    public void setClientCredits(Client client) throws Exception {
        if (client == null)
            throw new Exception("Invalid client");
        clientCredits = client.getCredits();
    }

    public void selectCredit(int index) throws Exception {
        int size = clientCredits.size();
        if (1 <= index && index <= size) selectedCredit = clientCredits.get(index - 1);
        else throw new IndexOutOfBoundsException("Nu exista un deposit cu asa numar");
    }

    public void printExtrasDeCont() {
        if (selectedCredit != null) selectedCredit.extras();
        else System.out.println("Selectati un cont pentru a afisa extrasul");
    }

    public void addMoney(Double amount) {
        if (selectedCredit != null) selectedCredit.payCredit(amount);
        else System.out.println("Selectati un cont pentru a putea adauga bani pe el");
    }

    // TODO: OpenCredit si CloseCredit
}
