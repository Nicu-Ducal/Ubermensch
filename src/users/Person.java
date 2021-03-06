package users;

public abstract class Person {
    protected final Integer id;
    protected String username;
    protected String password;
    protected String name;
    protected String email;

    public Person(int id, String username, String password, String name, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public Integer getID() { return id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getPassword() { return password; }

    public abstract String toString();

}
