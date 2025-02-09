package ksu.cs7530.obscura.model;

public class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
