package ksu.cs7530.obscura.view;

import ksu.cs7530.obscura.controller.ChatController;
import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatLoginView extends JFrame{

    // Restrict name and password fields to only support alphabetic characters
    public static class AlphaTextField extends JTextField {

        public AlphaTextField() {
            super();
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isLetter(c)) {
                        e.consume(); // Ignore the key press
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Not needed for this example
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Not needed for this example
                }
            });
        }

        public AlphaTextField(int columns) {
            super(columns);
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isLetter(c)) {
                        e.consume(); // Ignore the key press
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Not needed for this example
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Not needed for this example
                }
            });
        }
    }

    public static class AlphaPasswordField extends JPasswordField {

        public AlphaPasswordField() {
            super();
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isLetter(c)) {
                        e.consume(); // Ignore the key press
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Not needed for this example
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Not needed for this example
                }
            });
        }

        public AlphaPasswordField(int columns) {
            super(columns);
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isLetter(c)) {
                        e.consume(); // Ignore the key press
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Not needed for this example
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Not needed for this example
                }
            });
        }
    }

    private JRadioButton insecurePlaintextRadioButton;
    private JRadioButton securePrivateKey3DESRadioButton;
    private JRadioButton securePublicKeyRSARadioButton;
    private JButton startChatSessionButton;
    private AlphaTextField nameField;
    private JLabel ObscuraChat;
    private JButton closeButton;
    private JPanel securityPanel;
    private JPanel mainPanel;
    private AlphaPasswordField passwordField;
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

            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(startChatSessionButton);
            if (parent != null)
            {
                if(! verifyNamePasswordPair(parent))
                    return;
            }

            System.out.println("Start chat session pressed!");
            System.out.println("Username: " + nameField.getText());
            System.out.println("Password: " + new String(passwordField.getPassword()));

            System.out.println("Security selection: " + buttonGroup1.getSelection().getActionCommand());

            JFrame frame = new JFrame("ObscuraChat - Conversation");
            frame.setContentPane(new ChatConversationView(new User(nameField.getText()),
                    buttonGroup1.getSelection().getActionCommand(), null,true).mainPanel);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setResizable(false);
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
                if(verifyNamePasswordPair(frame))
                    createAndShowModalDialog(frame);
            }
        }
    });

    }

    private boolean verifyNamePasswordPair(JFrame parent)
    {
        boolean out = true;

        String name =  nameField.getText().toLowerCase();
        String password = new String(passwordField.getPassword()).toLowerCase();

        System.out.println("Name: " + name + " password: " + password);

        // Enforce minimum field length to keep someone from leaving it blank
        if(name.length() < 4 || password.length() < 4)
            out = false;
        else
        {
            // Perform simple ROT13 comparison, a shift of 13 letters
            for (int i = 0; i < name.length(); i++) {
                // Subtract the ASCII value for lower case 'a' to be zero based
                int nameChar = name.charAt(i) - 97;
                int passwordChar = password.charAt(i) - 97;

                if (((nameChar + 13) % 26) != passwordChar) {
                    out = false;
                    break;
                }
            }
        }

        // Show a message if they failed verification
        if(! out)
            JOptionPane.showMessageDialog(parent, "Incorrect name and password pair.");

        return out;
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
                frame.setResizable(false);
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
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
