package ksu.cs7530.obscura.controller;

import ksu.cs7530.obscura.model.User;

import javax.swing.*;

public class ChatController {

    static public String CHAT_SECURITY_PLAIN = "PLAIN";
    static public String CHAT_SECURITY_PRIVATE_KEY = "PRIVATE_KEY";
    static public String CHAT_SECURITY_PUBLIC_KEY = "PUBLIC_KEY";

    static private ChatController instance;
    static public ChatController getInstance()
    {
        if(instance == null)
            instance = new ChatController();

        return instance;
    }

    private ChatController()
    {
        System.out.println("Singleton instance of ChatController created");
    }

    public void startChatListener(User aUser, JTextArea textPane)
    {
        textPane.append("Starting listener for " + aUser.getName() + "...");
    }

}
