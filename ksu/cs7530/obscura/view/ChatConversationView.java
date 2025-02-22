package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.ChatListener;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatConversationView implements ChatListener {

    private User localUser;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton quitButton;
    private JButton sendButton;
    private JButton loadPublicWriteKeyButton;
    private JButton loadPrivateReadKeyButton;
    JPanel mainPanel;

public ChatConversationView(User localUser, String securityMode, String ipAddress, boolean startAsListener) {

    this.localUser = localUser;

    if(securityMode.equals(ChatController.CHAT_SECURITY_PRIVATE_KEY))
    {
        loadPublicWriteKeyButton.setVisible(false);
    }
    else if (securityMode.equals(ChatController.CHAT_SECURITY_PLAIN) )
    {
        loadPublicWriteKeyButton.setVisible(false);
        loadPrivateReadKeyButton.setVisible(false);
    }

    quitButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Close chat session pressed!");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(quitButton);
            if (frame != null) {
                frame.dispose();
            }

            ChatController.getInstance().close();
        }
    });
    sendButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            processLocalChatInput();
        }
    });
    loadPublicWriteKeyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Load public key pressed!");
        }
    });
    loadPrivateReadKeyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Load private key pressed!");
        }
    });
    textField1.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);

            System.out.println("Keyboard pressed from text field!");
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                processLocalChatInput();
        }
    });

    if(startAsListener)
        ChatController.getInstance().startChatSession(localUser, this);
    else
        ChatController.getInstance().joinChatSession(localUser, this, ipAddress);
}

    public void chatMessageReceived(User aUser, String message)
    {
        System.out.println("Received chat message event");
        textArea1.append("User[" + aUser.getName() + "]: " + message + "\n");
    }
    public void chatSessionEnded(User aUser)
    {
        System.out.println("Received session ended event");
    }

    private void processLocalChatInput()
    {
        if(! textField1.getText().isEmpty())
        {
            System.out.println("Send message to remote user");
            textArea1.append("User[" + localUser.getName() + "]: " + textField1.getText() + "\n");
            ChatController.getInstance().sendMessage(textField1.getText());
            textField1.setText("");
        }
    }
}
