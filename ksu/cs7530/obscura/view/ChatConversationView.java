package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.ChatListener;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.Semaphore;

public class ChatConversationView implements ChatListener {

    private User localUser;
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton quitButton;
    private JButton sendButton;
    private JButton loadPublicWriteKeyButton;
    private JButton loadPrivateReadKeyButton;
    JPanel mainPanel;
    Semaphore verifySemaphore = new Semaphore(0);
    boolean verifyResult = false;

public ChatConversationView(User localUser, String securityMode, String ipAddress, boolean startAsListener) {

    this.localUser = localUser;

    if (securityMode.equals(ChatController.CHAT_SECURITY_PLAIN) || securityMode.equals(ChatController.CHAT_SECURITY_PRIVATE_KEY))
    {
        loadPublicWriteKeyButton.setVisible(false);
        loadPrivateReadKeyButton.setVisible(false);
    }

    textField1.setEnabled(false);
    sendButton.setEnabled(false);

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
        ChatController.getInstance().startChatSession(localUser, this, securityMode);
    else
        ChatController.getInstance().joinChatSession(localUser, this, ipAddress, securityMode);
}

    public void chatMessageReceived(User aUser, String message)
    {
        System.out.println("Received chat message event");
        textArea1.append("User[" + aUser.getName() + "]:  " + message + "\n");
    }
    public void chatSessionEnded(User aUser)
    {
        System.out.println("Received session ended event");
    }

    public boolean confirmChatSession(User aUser, String security)
    {
        System.out.println("Confirm chat session with user = " + aUser.getName() + " security = " + security);

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(quitButton);
        if (frame != null)
        {
            createAndShowModalDialog(frame, aUser, security);
        }

        try { verifySemaphore.acquire(); } catch (Exception e) { e.printStackTrace(); }
        return verifyResult;
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

    private void createAndShowModalDialog(JFrame parent, User aUser, String security) {
        JDialog dialog = new JDialog(parent, "Verify Connection", true); // true makes it modal
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Chat with User = " + aUser.getName() + " using " + security + " security"));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(inputPanel);

        // Add conditional input field for private security key
        JTextField privateKeyField = null;
        if(security.equals(ChatController.CHAT_SECURITY_PRIVATE_KEY))
        {
            JPanel keyPanel = new JPanel();
            privateKeyField = new JTextField(20);
            keyPanel.add(new JLabel("Hexadecimal Key:"));
            keyPanel.add(privateKeyField);
            keyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dialog.add(keyPanel);
        }

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("Accept");
        JButton cancelButton = new JButton("Reject");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialog.add(buttonPanel);

        JTextField finalPrivateKeyField = privateKeyField;
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose(); // Close the dialog

                // Set the private key on the chat controller if we have a private key
                if(finalPrivateKeyField != null)
                    ChatController.getInstance().setPrivateKey(finalPrivateKeyField.getText());

                verifyResult = true;
                verifySemaphore.release();

                parent.setTitle("ObscuraChat - Conversation (" + aUser.getName() + ")");
                textField1.setEnabled(true);
                sendButton.setEnabled(true);
                chatMessageReceived(User.SYSTEM, "Accepted ObscuraChat With: " + aUser.getName());
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dialog.dispose(); // Close the dialog
                verifyResult = false;
                verifySemaphore.release();

                chatMessageReceived(User.SYSTEM, "Rejected ObscuraChat With: " + aUser.getName());
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(parent); // Center relative to the parent frame
        dialog.setVisible(true);
    }
}