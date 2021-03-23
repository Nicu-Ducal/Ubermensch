package services;

import users.Client;

import java.util.List;

public class ClientService {
    private List<Client> clientList;
    private Client currentClient;

    public ClientService(List<Client> clientList) {
        this.clientList = clientList;
        this.currentClient = null;
    }

    public Boolean LogIn(String username, String password) {
        for (Client cl: clientList) {
            if (cl.getUsername().equals(username) && cl.checkPassword(password)) {
                currentClient = cl;
                return true;
            }
        }
        return false;
    }

    public Boolean LogOut() {
        if (currentClient != null) {
            currentClient = null;
            return true;
        }
        return false;
    }

    
}