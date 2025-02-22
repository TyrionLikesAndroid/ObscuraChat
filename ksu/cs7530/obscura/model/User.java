package ksu.cs7530.obscura.model;

public class User {
    private final String name;
    public static final User SYSTEM = new User("System");

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
