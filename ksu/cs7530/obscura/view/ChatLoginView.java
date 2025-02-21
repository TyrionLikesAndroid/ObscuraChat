package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatLoginView extends JFrame{
    private JRadioButton insecurePlaintextRadioButton;
    private JRadioButton securePrivateKey3DESRadioButton;
    private JRadioButton securePublicKeyRSARadioButton;
    private JButton startChatSessionButton;
    private JTextField nameField;
    private JLabel ObscuraChat;
    private JButton closeButton;
    private JPanel securityPanel;
    private JPanel mainPanel;
    private JPasswordField passwordField;
    private JButton joinChatButton;
    private ButtonGroup buttonGroup1;

    public ChatLoginView() {

        System.out.println("Executed the constructor");
        insecurePlaintextRadioButton.setActionCommand(ChatController.CHAT_SECURITY_PLAIN);
        securePrivateKey3DESRadioButton.setActionCommand(ChatController.CHAT_SECURITY_PRIVATE_KEY);
        securePublicKeyRSARadioButton.setActionCommand(ChatController.CHAT_SECURITY_PUBLIC_KEY);

    startChatSessionButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Start chat session pressed!");
            System.out.println("Username: " + nameField.getText());
            System.out.println("Password: " + new String(passwordField.getPassword()));

            System.out.println("Security selection: " + buttonGroup1.getSelection().getActionCommand());

            JFrame frame = new JFrame("ObscuraChat - Conversation");
            frame.setContentPane(new ChatConversationView(new User(nameField.getText()),
                    buttonGroup1.getSelection().getActionCommand(), null,true).mainPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            closeButton.doClick();
        }
    });

    closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Close chat session pressed!");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(closeButton);
            if (frame != null) {
                frame.dispose();
            }
        }
    });

    joinChatButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println("Join chat session pressed!");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(joinChatButton);
            if (frame != null)
            {
                createAndShowModalDialog(frame);
            }
        }
    });

    }

    private void createAndShowModalDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Chat Connection Setup", true); // true makes it modal
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JTextField textField = new JTextField(20);
        inputPanel.add(new JLabel("IP Address:"));
        inputPanel.add(textField);
        dialog.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("Connect");
        JButton cancelButton = new JButton("Quit");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                System.out.println("Entered text: " + text); // Or do something with the text
                dialog.dispose(); // Close the dialog

                JFrame frame = new JFrame("ObscuraChat - Conversation");
                frame.setContentPane(new ChatConversationView(new User(nameField.getText()),
                        buttonGroup1.getSelection().getActionCommand(), text,false).mainPanel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);

                closeButton.doClick();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(parent); // Center relative to the parent frame
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ObscuraChat - Login");
        frame.setContentPane(new ChatLoginView().mainPanel); // Use the main panel
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
