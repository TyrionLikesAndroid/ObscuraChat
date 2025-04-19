package ksu.cs7530.obscura.controller;

import ksu.cs7530.obscura.encryption.Cryptosystem;
import ksu.cs7530.obscura.encryption.RSACryptosystem;
import ksu.cs7530.obscura.encryption.TripleDESCryptosystem;
import ksu.cs7530.obscura.model.ChatListener;
import ksu.cs7530.obscura.model.User;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatController implements Runnable {

    static private ChatController instance;
    static public String CHAT_SECURITY_PLAIN = "PLAIN";
    static public String CHAT_SECURITY_PRIVATE_KEY = "PRIVATE_KEY";
    static public String CHAT_SECURITY_PUBLIC_KEY = "PUBLIC_KEY";
    static public String CHAT_TAG = "<OBSCURA_CHAT>";
    static private final int PORT = 53737;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String remoteIpAddress = null;
    private PrintWriter output;
    private BufferedReader input;
    private ChatListener chatListener;
    private User localUser;
    private User remoteUser;
    private String securityMode;
    private String privateHexKey;
    private Cryptosystem cryptosystem;
    boolean encryptionEnabled = false;

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

    public void startChatSession(User aUser, ChatListener aListener, String mode) {

        chatListener = aListener;
        localUser = aUser;
        securityMode = mode;

        InetAddress localIP = ChatController.getLocalIPAddress();
        if (localIP != null)
        {
            chatListener.chatMessageReceived(User.SYSTEM,
                    "Starting listener for " + aUser.getName() + " on " + localIP.getHostAddress());
           new Thread(instance).start();
        }
    }

    public void joinChatSession(User aUser, ChatListener aListener, String ipAddress, String mode) {

        remoteIpAddress = ipAddress;
        localUser = aUser;
        securityMode = mode;

        chatListener = aListener;
        chatListener.chatMessageReceived(User.SYSTEM, "Connecting to existing session on " + ipAddress);
        new Thread(instance).start();
    }

    public void sendMessage(String message) {

        if(output != null)
        {
            System.out.println("Sending message: " + message);
            output.println(encryptionEnabled ? cryptosystem.encrypt(message, false): message);
        }
    }

    public void close()
    {
        try
        {
            if(serverSocket != null)
                serverSocket.close();

            if(clientSocket != null)
                clientSocket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        if(remoteIpAddress == null)
        {
            try
            {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Server started, waiting for connection...");

                while (true)
                {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Get input stream (to receive messages from client)
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // Get output stream (to send messages to client)
                    output = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Echo to listener that a remote connection has been established
                    chatListener.chatMessageReceived(User.SYSTEM, "Remote Connection Established");

                    // Verify that the person at the other end is someone we want to chat with
                    if(! verifyRemoteConnection())
                        continue;

                    // Verify that the person at the other end has a compatible security key
                    if(! verifySecurityCompatibility())
                    {
                        continue;
                    }

                    readMessageLoop();
                }
            }
            catch (IOException ioException)
            {
                System.out.println("IOException caught in start connection loop: " + ioException.toString());
            }
        }
        else
        {
            try
            {
                // Try to connect to an existing listener
                clientSocket = new Socket(remoteIpAddress, PORT);

                // Get input stream (to send messages to server)
                output = new PrintWriter(clientSocket.getOutputStream(), true);
                // Get input stream (to receive messages from server)
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Send a message to the server
                chatListener.chatMessageReceived(User.SYSTEM, "Remote Connection Established");

                // Verify that the person at the other end is someone we want to chat with
                if(! verifyRemoteConnection())
                {
                    input.close();
                    output.close();
                    clientSocket.close();
                    return;
                }

                // Verify that the person at the other end has a compatible security key
                if(! verifySecurityCompatibility())
                {
                    input.close();
                    output.close();
                    clientSocket.close();
                    return;
                }

                readMessageLoop();
            }
            catch (IOException ioException)
            {
                System.out.println("IOException caught in join connection loop: " + ioException.toString());
            }
        }
    }

    private void readMessageLoop()
    {
        System.out.println("Starting read message loop");

        try
        {
            // Read messages from the client constantly until the connection is dropped
            String message;
            String plainMessage;
            while ((message = input.readLine()) != null)
            {
                plainMessage = encryptionEnabled ? cryptosystem.decrypt(message, false): message;

                // See if this is a control message or a normal payload message
                Pattern pattern = Pattern.compile(CHAT_TAG + "(ECB|CBC|CTR)" + CHAT_TAG);
                Matcher matcher = pattern.matcher(plainMessage);
                if(matcher.matches())
                {
                    String mode = plainMessage.substring(CHAT_TAG.length(), plainMessage.length() - CHAT_TAG.length());
                    chatListener.chatOperationalModeChange(mode);
                    cryptosystem.setOperationalMode(mode);
                    chatListener.chatMessageReceived(remoteUser, "Changing operational mode to [" + mode + "]");
                }
                else
                    chatListener.chatMessageReceived(remoteUser, plainMessage);
            }

            System.out.println("Fallen out of read message loop");
        }
        catch (IOException ioException)
        {
            System.out.println("IOException caught in read loop: " + ioException.toString());
        }
    }

    private boolean verifyRemoteConnection()
    {
        boolean out = false;

        try
        {
            // Send the verification handshake
            sendMessage(CHAT_TAG + localUser.getName() + "," + securityMode + CHAT_TAG);

            // Wait on the verification response - THIS IS A BLOCKING CALL
            String message = input.readLine();

            // Confirm that the protocol is correct for an ObscuraChat client
            Pattern pattern = Pattern.compile(CHAT_TAG + "[a-zA-Z0-9_]+,[a-zA-Z0-9_]+" + CHAT_TAG);
            Matcher matcher = pattern.matcher(message);
            if(matcher.matches())
            {
                int startIndex = CHAT_TAG.length();
                int commaIndex = message.indexOf(",");
                remoteUser = new User(message.substring(startIndex,commaIndex));
                String desiredSecurity = message.substring(commaIndex + 1, message.length() - CHAT_TAG.length());
                return chatListener.confirmChatSession(remoteUser, desiredSecurity);
            }
            else
            {
                System.out.println("Handshake string didn't match required protocol: " + message);
                return false;
            }
        }
        catch (IOException ioException)
        {
            System.out.println("IOException caught in verifyRemoteConnection: " + ioException.toString());
        }

        return out;
    }

    private boolean verifySecurityCompatibility()
    {
        boolean out = true;
        try
        {
            // If we have a security mode other than plain, verify our key setup.  This is a blocking call
            // until we are able to verify both ends otherwise we will just see gibberish
            if(! securityMode.equals(CHAT_SECURITY_PLAIN))
            {
                chatListener.chatMessageReceived(User.SYSTEM, "Verifying security compatibility");

                if(securityMode.equals(CHAT_SECURITY_PRIVATE_KEY))
                {
                    System.out.println("Local Hex key is " + privateHexKey);

                    // Create our DES cryptosystem instance now that we know our private key
                    cryptosystem = new TripleDESCryptosystem(privateHexKey);

                    // Send a test message to make sure we can communicate successfully
                    encryptionEnabled = sendTestMessage();
                }
                else if(securityMode.equals(CHAT_SECURITY_PUBLIC_KEY))
                {
                    // Create a key set that we can use for our conversation.  Both sides will need to set up
                    // their own private keys to have a two-way conversation
                    RSACryptosystem rsaCryptosystem = new RSACryptosystem();
                    cryptosystem = rsaCryptosystem;

                    // Send the public key and value of n, the other side will be doing the same
                    sendMessage(CHAT_TAG + rsaCryptosystem.localKeys.publicKey + "," + rsaCryptosystem.localKeys.n + CHAT_TAG);

                    // Wait on the verification response - THIS IS A BLOCKING CALL
                    String message = input.readLine();
                    String remotePubKey;
                    String remoteNValue;

                    // Read the public key and n from the remote endpoint
                    Pattern pattern = Pattern.compile(CHAT_TAG + "[A-F0-9]+,[A-F0-9]+" + CHAT_TAG);
                    Matcher matcher = pattern.matcher(message);
                    if(matcher.matches())
                    {
                        int startIndex = CHAT_TAG.length();
                        int commaIndex = message.indexOf(",");
                        remotePubKey = message.substring(startIndex,commaIndex);
                        remoteNValue = message.substring(commaIndex + 1, message.length() - CHAT_TAG.length());

                        System.out.println("Received remote keyset successfully");
                    }
                    else
                    {
                        System.out.println("Handshake string didn't match required protocol: " + message);
                        return false;
                    }

                    // If we get here, we got a valid set of remote keys so send them to our ChatController
                    rsaCryptosystem.setRemoteKeyset(new BigInteger(remotePubKey), new BigInteger(remoteNValue));

                    // Send a test message to make sure we can communicate successfully
                    encryptionEnabled = sendTestMessage();
                }

                out = encryptionEnabled;
                cryptosystem.setOperationalMode(Cryptosystem.CHAT_OPERATIONAL_MODE_ECB);
                String testResult = encryptionEnabled ? "SUCCESS" : "FAILURE";
                chatListener.chatMessageReceived(User.SYSTEM, "Security compatibility test [" + testResult + "]");
            }

            // Any condition where our output is true is a session start (plain text or good security handshake)
            if(out)
                chatListener.chatSessionStarted(securityMode);
        }
        catch (IOException ioException)
        {
            System.out.println("IOException caught in verifySecurityCompatibility: " + ioException.toString());
            encryptionEnabled = false;
            out = false;
        }

        return out;
    }

    public void changeOperationalMode(String mode)
    {
        chatListener.chatMessageReceived(localUser, "Changing operational mode to [" + mode + "]");

        // Send the new operational mode in the normal message band
        sendMessage(CHAT_TAG + mode + CHAT_TAG);

        // Tell the cryptosystem to change operational mode, both sides will do it automatically
        cryptosystem.setOperationalMode(mode);
    }

    private boolean sendTestMessage()
    {
        try
        {
            // Send a test message to make sure we can communicate successfully
            String encryptTestMsg = cryptosystem.encrypt(CHAT_TAG + remoteUser.getName() + CHAT_TAG, false);
            System.out.println("Encrypt test request - " + encryptTestMsg);

            // Send the private key compatibility string and wait on the response
            sendMessage(encryptTestMsg);

            // Wait on the verification response - THIS IS A BLOCKING CALL
            String cipherMessage = input.readLine();
            String encryptTestResponse = cryptosystem.decrypt(cipherMessage, false);
            System.out.println("Encrypt test response - " + encryptTestResponse);

            return encryptTestResponse.equals(CHAT_TAG + localUser.getName() + CHAT_TAG);
        }
        catch (IOException ioException)
        {
            System.out.println("IOException caught in sendTestMessage: " + ioException.toString());
            return false;
        }
    }

    public void setPrivateKey(final String hexKey)
    {
        this.privateHexKey = hexKey;
    }

    public static InetAddress getLocalIPAddress()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface iface = interfaces.nextElement();
                // We want an interface that is up, not loopback, and has an IP address.
                if (iface.isUp() && !iface.isLoopback() && iface.getInetAddresses().hasMoreElements())
                {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements())
                    {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof java.net.Inet4Address)
                        {
                            return addr;  // Found a suitable address
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null; // Could not find a suitable address
    }
}