package ksu.cs7530.obscura.controller;

import ksu.cs7530.obscura.model.User;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class ChatController implements Runnable {

    private static final int PORT = 12345; // Example port

    static public String CHAT_SECURITY_PLAIN = "PLAIN";
    static public String CHAT_SECURITY_PRIVATE_KEY = "PRIVATE_KEY";
    static public String CHAT_SECURITY_PUBLIC_KEY = "PUBLIC_KEY";
    JTextArea textOutput;
    ServerSocket serverSocket;

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

    public void startChatListener(User aUser, JTextArea textPane) {

        textOutput = textPane;
        InetAddress localIP = ChatController.getLocalIPAddress();
        if (localIP != null)
        {
            textOutput.append("Starting listener for " + aUser.getName() + " on " + localIP.getHostAddress() +
                    "/" + PORT);
            new Thread(instance).start();
        }
    }

    public void close()
    {
        try
        {
            serverSocket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started, waiting for connection...");

            while(true)
            {
                // Accept client connection
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Get input stream (to receive messages from client)
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // Get output stream (to send messages to client)
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                    String message;
                    // Read messages from the client
                    while ((message = input.readLine()) != null) {
                        textOutput.append("\n" + message);
                        output.println("Server received: " + message);
                    }
                }
                System.out.println("Fallen out of read loop");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static InetAddress getLocalIPAddress()
    {
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // We want an interface that is up, not loopback, and has an IP address.
                if (iface.isUp() && !iface.isLoopback() && iface.getInetAddresses().hasMoreElements()) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        // Prefer IPv4 addresses (IPv6 can be complex)
                        if (addr instanceof java.net.Inet4Address) {
                            return addr;  // Found a suitable address
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null; // Could not find a suitable address
    }

}
