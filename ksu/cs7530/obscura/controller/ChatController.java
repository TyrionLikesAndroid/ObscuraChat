package ksu.cs7530.obscura.controller;

import ksu.cs7530.obscura.model.ChatListener;
import ksu.cs7530.obscura.model.User;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class ChatController implements Runnable {

    static private ChatController instance;
    static public String CHAT_SECURITY_PLAIN = "PLAIN";
    static public String CHAT_SECURITY_PRIVATE_KEY = "PRIVATE_KEY";
    static public String CHAT_SECURITY_PUBLIC_KEY = "PUBLIC_KEY";
    static private final int PORT = 53737;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String remoteIpAddress = null;
    private PrintWriter output;
    private BufferedReader input;
    private ChatListener chatListener;

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

    public void startChatSession(User aUser, ChatListener aListener) {

        chatListener = aListener;

        InetAddress localIP = ChatController.getLocalIPAddress();
        if (localIP != null)
        {
            chatListener.chatMessageReceived(User.SYSTEM,
                    "Starting listener for " + aUser.getName() + " on " + localIP.getHostAddress());
           new Thread(instance).start();
        }
    }

    public void joinChatSession(User aUser, ChatListener aListener, String ipAddress) {

        remoteIpAddress = ipAddress;

        chatListener = aListener;
        chatListener.chatMessageReceived(User.SYSTEM, "Connecting to existing session on " + ipAddress);
        new Thread(instance).start();
    }

    public void sendMessage(String message) {

        if(output != null) {
            System.out.println("Sending message: " + message);
            output.println(message);
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
        try
        {
            // Read messages from the client constantly until the connection is dropped
            String message;
            while ((message = input.readLine()) != null)
            {
                chatListener.chatMessageReceived(new User("REMOTE"), message);
            }

            System.out.println("Fallen out of read message loop");
        }
        catch (IOException ioException)
        {
            System.out.println("IOException caught in read loop: " + ioException.toString());
        }
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