package users;

public class Employee extends Person {
    private String job;

    public Employee(int id, String username, String password, String name, String email, String job) {
        super(id, username, password, name, email);
        this.job = job;
    }

    @Override
    public String toString() {
        return ("Informatii despre clientul " + name + ":\n"
                + "Username: " + username + "\nEmail: " + email
                + "\nJob: " + job + "\n");
    }
}
