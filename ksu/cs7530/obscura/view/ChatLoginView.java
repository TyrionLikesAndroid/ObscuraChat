package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
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
                    buttonGroup1.getSelection().getActionCommand()).mainPanel);
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
}
    public static void main(String[] args) {
        JFrame frame = new JFrame("ObscuraChat - Login");
        frame.setContentPane(new ChatLoginView().mainPanel); // Use the main panel
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
