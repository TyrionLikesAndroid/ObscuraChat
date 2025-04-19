package ksu.cs7530.obscura.model;

public interface ChatListener {

    public void chatMessageReceived(User aUser, String message);
    public void chatSessionEnded(User aUser);
    public void chatSessionStarted(String security);
    public boolean confirmChatSession(User aUser, String security);
}