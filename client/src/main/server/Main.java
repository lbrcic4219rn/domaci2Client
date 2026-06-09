package main.server;

import main.server.threads.BroadcastThread;
import main.server.threads.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static final int PORT = 9000;

    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        Thread broadcast = new Thread(new BroadcastThread(server));
        broadcast.start();

        try (ServerSocket ss = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket client = ss.accept();
                new Thread(new ServerThread(client, server)).start();
            }
        } catch (IOException e) {
            System.out.println("Problem connecting to the client");
        }

    }
}
