package ru.chat.client;

import ru.network.TCPConnection;
import ru.network.TCPConnectionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static String ipAddress;
    private static final int port = 8000;
    private static final int widthWindow = 640;
    private static final int heightWindow = 480;

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField();
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow(){

        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostName();

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(widthWindow, heightWindow);
        setLocationRelativeTo(null);
        //setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldNickName, BorderLayout.NORTH);
        add(fieldInput, BorderLayout.SOUTH);


        setVisible(true);
        try {
            connection = new TCPConnection(this, ipAddress, port);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")){
            return;
        }
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

}
