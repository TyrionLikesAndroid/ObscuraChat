package ksu.cs7530.obscura.model;

public interface ChatListener {

    public void chatMessageReceived(User aUser, String message);
    public void chatSessionEnded(User aUser);
}