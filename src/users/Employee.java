package users;

public class Employee extends Person {
    private String job;

    public Employee(int id, String username, String password, String name, String email, String job) {
        super(id, username, password, name, email);
        this.job = job;
    }

    public void signContract() {

    }
}
