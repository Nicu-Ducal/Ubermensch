package users;

public class Client extends Person {
    private String clientType;

    public Client(int id, String username, String password, String name, String email, String clientType) {
        super(id, username, password, name, email);
        this.clientType = clientType;
    }

    public void signContract() {

    }
}
