package main.client;

import main.client.threads.ReaderThread;
import main.client.threads.WriterThread;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 9000;

    public static void main(String[] args) {
        AtomicBoolean running = new AtomicBoolean(true);

        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Connected to the server at " + HOST + ":" + PORT);

            Thread reader = new Thread(new ReaderThread(socket, running));
            Thread writer = new Thread(new WriterThread(socket, running));
            reader.start();
            writer.start();

            writer.join();
            running.set(false);
            socket.close();
            reader.join();
            System.out.println("Client terminated gracefully.");

        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Application interrupted. ");
        }
    }
}
