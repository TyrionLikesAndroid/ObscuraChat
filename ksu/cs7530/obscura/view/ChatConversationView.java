package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatConversationView {

    private User localUser;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton quitButton;
    private JButton sendButton;
    private JButton loadPublicWriteKeyButton;
    private JButton loadPrivateReadKeyButton;
    JPanel mainPanel;

public ChatConversationView(User localUser, String securityMode) {

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

        }
    });
    sendButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Send message pressed!");
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
        }
    });

    ChatController.getInstance().startChatListener(localUser, textArea1);
}
}
