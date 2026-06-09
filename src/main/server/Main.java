package main.server;

import main.server.threads.BroadcastThread;
import main.server.threads.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final int PORT = 9000;

    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        Thread broadcast = new Thread(new BroadcastThread(server));
        broadcast.start();

        try (var ss = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket client = ss.accept();
                new Thread(new ServerThread(client, server)).start();
            }
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Problem accepting client connections", e);
        }

    }
}
